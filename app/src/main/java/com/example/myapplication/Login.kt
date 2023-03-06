package com.example.myapplication

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.stream.Collectors
import com.sun.net.httpserver.HttpServer;

class Login {
    var lichessUri = "https://lichess.org"

    /**
     * This demo application will launch a Web Browser,
     * where authentication with Lichess can be made,
     * for authorization of this demo application to
     * request the e-mail address of the authenticating
     * Lichess user - and if granted - the e-mail address
     * will be fetched and printed on standard output.
     */
    fun main(args: Array<String>) {

        // Perform the OAuth2 PKCE flow
        val access_token = login()

        // Fetch the e-mail address
        val email = readEmail(access_token)
        println("e-mail: $email")

        // Logout
        logout(access_token)
    }

    @Throws(Exception::class)
    fun login(): String {

        // Prepare a new login.
        // We will generate a lot of parameters which will be used in this login,
        // and then the parameters are thrown away, not to be re-used.
        // I.e, next login request will have new parameters generated.

        // Setup a local bind address which we will use in redirect_uri
        val local = InetSocketAddress(InetAddress.getLoopbackAddress(), 0)
        val httpServer = HttpServer.create(local, 0)
        val redirectHost = local.address.hostAddress
        val redirectPort: Int = httpServer.getAddress().getPort()
        val code_verifier = generateRandomCodeVerifier()
        val code_challenge_method = "S256"
        val code_challenge = generateCodeChallenge(code_verifier)
        val response_type = "code"
        val client_id = "apptest"
        val redirect_uri = "http://$redirectHost:$redirectPort/"
        val scope = "email:read"
        val state = generateRandomState()
        val parameters = java.util.Map.of(
            "code_challenge_method", code_challenge_method,
            "code_challenge", code_challenge,
            "response_type", response_type,
            "client_id", client_id,
            "redirect_uri", redirect_uri,
            "scope", scope,
            "state", state
        )
        val paramString = parameters.entries.stream()
            .map { (key, value): Map.Entry<String, String> -> "$key=$value" }
            .collect(Collectors.joining("&"))

        // Front Channel URL, all these parameters are non-sensitive.
        // The actual authentication between User and Lichess happens outside of this demo application,
        // i.e in the browser over HTTPS.
        val frontChannelUrl = URI.create(lichessUri + "/oauth" + "?" + paramString)

        // Prepare for handling the upcoming redirect,
        // after User has authenticated with Lichess,
        // and granted this demo application permission
        // to fetch the e-mail address.
        // The random code_verifier we generated for this single login,
        // will be sent to Lichess on a "Back Channel" so they can verify that
        // the Front Channel request really came from us.
        val cf = registerRedirectHandler(httpServer, parameters, code_verifier)

        // Now we let the User authorize with Lichess,
        // using their browser
        if (java.awt.Desktop.isDesktopSupported()) {
            val desktop: java.awt.Desktop = java.awt.Desktop.getDesktop()
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                desktop.browse(frontChannelUrl)
            } else {
                System.out.format(
                    "%s%n%n%s%n  %s%n%n",
                    "Doh, Desktop.Action.BROWSE not supported...",
                    "Could you manually go to the following URL :) ?",
                    frontChannelUrl
                )
            }
        } else {
            System.out.format(
                "%s%n%n%s%n  %s%n%n",
                "Doh, Desktop not supported...",
                "Could you manually go to the following URL :) ?",
                frontChannelUrl
            )
        }

        // Blocking until user has authorized,
        // and we've exchanged the incoming authorization code for an access token
        val access_token = cf.get()
        httpServer.stop(0)
        return access_token
    }

    @Throws(Exception::class)
    fun readEmail(access_token: String): String? {

        // Get that e-mail
        val emailRequest: java.net.http.HttpRequest = java.net.http.HttpRequest.newBuilder(
            URI.create(
                lichessUri + "/api/account/email"
            )
        )
            .GET()
            .header("authorization", "Bearer $access_token")
            .header("accept", "application/json")
            .build()
        val response: java.net.http.HttpResponse<String> = java.net.http.HttpClient.newHttpClient()
            .send<String>(emailRequest, java.net.http.HttpResponse.BodyHandlers.ofString())
        val statusCode: Int = response.statusCode()
        val body: String = response.body()
        val email = parseField("email", body)
        if (statusCode != 200) {
            println("/api/account/email - $statusCode")
        }
        return email
    }

    @Throws(Exception::class)
    fun logout(access_token: String) {
        val logoutRequest: java.net.http.HttpRequest = java.net.http.HttpRequest.newBuilder(
            URI.create(
                lichessUri + "/api/token"
            )
        )
            .DELETE()
            .header("authorization", "Bearer $access_token")
            .build()
        val response: java.net.http.HttpResponse<Void> = java.net.http.HttpClient.newHttpClient()
            .send<Void>(logoutRequest, java.net.http.HttpResponse.BodyHandlers.discarding())
        val statusCode: Int = response.statusCode()
        if (statusCode != 204) {
            println("/api/token - " + response.statusCode())
        }
    }

    fun generateRandomCodeVerifier(): String {
        val bytes = ByteArray(32)
        Random().nextBytes(bytes)
        return encodeToString(bytes)
    }

    fun generateCodeChallenge(code_verifier: String): String {
        val asciiBytes =
            code_verifier.toByteArray(StandardCharsets.US_ASCII)
        val md: MessageDigest
        md = try {
            MessageDigest.getInstance("SHA-256")
        } catch (nsa_ehhh: NoSuchAlgorithmException) {
            throw RuntimeException(nsa_ehhh)
        }
        val s256bytes = md.digest(asciiBytes)
        return encodeToString(s256bytes)
    }

    fun generateRandomState(): String {
        val bytes = ByteArray(16)
        Random().nextBytes(bytes)
        // Not sure how long the parameter "should" be,
        // going for 8 characters here...
        return encodeToString(bytes).substring(0, 8)
    }

    fun encodeToString(bytes: ByteArray?): String {
        return Base64.getUrlEncoder().encodeToString(bytes)
            .replace("=".toRegex(), "")
            .replace("\\+".toRegex(), "-")
            .replace("\\/".toRegex(), "_")
    }

    fun registerRedirectHandler(
        httpServer: com.sun.net.httpserver.HttpServer,
        requestParams: Map<String, String>,
        code_verifier: String?
    ): CompletableFuture<String> {
        val cf = CompletableFuture<String>()
        httpServer.createContext("/",
            com.sun.net.httpserver.HttpHandler { exchange: com.sun.net.httpserver.HttpExchange ->
                httpServer.removeContext("/")

                // The redirect arrives...
                val query: String = exchange
                    .getRequestURI()
                    .getQuery()
                val inparams =
                    Arrays.stream(
                        query.split("&".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray())
                        .collect(
                            Collectors.toMap(
                                Function { s: String ->
                                    s.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                                        .toTypedArray()[0]
                                },
                                Function { s: String ->
                                    s.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                                        .toTypedArray()[1]
                                }
                            ))
                val code = inparams["code"]
                val state = inparams["state"]
                if (state != requestParams["state"]) {
                    cf.completeExceptionally(Exception("The \"state\" parameter we sent and the one we recieved didn't match!"))
                    return@createContext
                }
                if (code == null) {
                    exchange.sendResponseHeaders(503, -1)
                    cf.completeExceptionally(Exception("Authorization Failed"))
                    return@createContext
                }

                // We have received meta data from Lichess,
                // about the fact that the User has authorized us - yay!

                // Let's respond with a nice HTML page in celebration.
                val responseBytes =
                    "<html><body><h1>Success, you may close this page</h1></body></html>".toByteArray()
                exchange.sendResponseHeaders(200, responseBytes.size.toLong())
                exchange.getResponseBody().write(responseBytes)


                // Now,
                // let's go to Lichess and ask for a token - using the meta data we've received
                val tokenParameters =
                    java.util.Map.of(
                        "code_verifier", code_verifier,
                        "grant_type", "authorization_code",
                        "code", code,
                        "redirect_uri", requestParams["redirect_uri"],
                        "client_id", requestParams["client_id"]
                    )
                val tokenParamsString = tokenParameters.entries.stream()
                    .map { (key, value): Map.Entry<String, String?> -> "$key=$value" }
                    .collect(Collectors.joining("&"))
                val tokenRequest: java.net.http.HttpRequest =
                    java.net.http.HttpRequest.newBuilder(URI.create(lichessUri + "/api/token"))
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(tokenParamsString))
                        .header("content-type", "application/x-www-form-urlencoded")
                        .build()
                try {
                    val response: java.net.http.HttpResponse<String> =
                        java.net.http.HttpClient.newHttpClient().send<String>(
                            tokenRequest,
                            java.net.http.HttpResponse.BodyHandlers.ofString()
                        )
                    val statusCode: Int = response.statusCode()
                    val body: String = response.body()
                    if (statusCode != 200) {
                        println("/api/token - $statusCode")
                    }
                    val access_token = parseField("access_token", body)
                    if (access_token == null) {
                        println("Body: $body")
                        cf.completeExceptionally(Exception("Authorization Failed"))
                        return@createContext
                    }

                    // Ok, we have successfully retrieved a token which we can use
                    // to fetch the e-mail address
                    cf.complete(access_token)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        httpServer.start()
        return cf
    }

    // Light-weight fragile "json" ""parser""...
    fun parseField(field: String, body: String): String? {
        return try {
            val start = body.indexOf(field) + field.length + 3
            val stop = body.indexOf("\"", start)
            body.substring(start, stop)
        } catch (e: Exception) {
            null
        }
    }
}