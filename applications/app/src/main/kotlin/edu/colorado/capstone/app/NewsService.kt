package edu.colorado.capstone.app

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NewsService {
    fun getNewsForTopic(topic: String): List<NewsItem> {
        return try {
            val url = URL("https://collector-app-573036605406.us-central1.run.app/search?topic=$topic")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                // Parse only top 5 articles, matching the NewsItem structure (title + url)
                val articles = Json { ignoreUnknownKeys = true }
                    .decodeFromString<List<NewsItem>>(response.toString())

                articles.take(5)
            } else {
                println("Error fetching news for topic $topic: HTTP error code $responseCode")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching news for topic $topic: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    fun getTopStories(): List<NewsItem> {
    return try {
        val url = URL("https://collector-app-573036605406.us-central1.run.app/news")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            val articles = Json { ignoreUnknownKeys = true }
                .decodeFromString<List<NewsItem>>(response.toString())

            articles.take(5) // ✅ Only show top 5 stories
        } else {
            println("Error fetching top stories: HTTP error code $responseCode")
            emptyList()
        }
    } catch (e: Exception) {
        println("Error fetching top stories: ${e.message}")
        e.printStackTrace()
        emptyList()
    }
}

}
