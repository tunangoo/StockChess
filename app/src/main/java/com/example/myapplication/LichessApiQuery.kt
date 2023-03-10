import android.app.Activity
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class LichessApiQuery(private val activity: Activity) {

    suspend fun getUserInfo(username: String): String? = withContext(Dispatchers.IO) {
        val url = URL("https://lichess.org/api/user/$username")
        val connection = url.openConnection() as HttpsURLConnection

        connection.requestMethod = "GET"

        val responseCode = connection.responseCode

        return@withContext if (responseCode == HttpsURLConnection.HTTP_OK) {
            val inputStreamReader = InputStreamReader(connection.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val response = StringBuffer()

            var inputLine: String?
            while (bufferedReader.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            bufferedReader.close()

            response.toString()
        } else {
            null
        }
    }

    suspend fun updateUserInfo(username: String) {
        val userInfo = getUserInfo(username)

        withContext(Dispatchers.Main) {
            if (userInfo != null) {
                Log.d("TAG", "Lichess: $userInfo")
                // Update the UI with the user info
            } else {
                // Show an error message
            }
        }
    }
}
