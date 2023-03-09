package com.example.myapplication
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class api {
    // Create URL
    var lichessEndpoint: URL = URL("https://lichess.org")

    // Create connection
    var myConnection: HttpsURLConnection = lichessEndpoint.openConnection() as HttpsURLConnection

}