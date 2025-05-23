package edu.colorado.capstone.email
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import com.rabbitmq.client.ConnectionFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class NewsArticle(
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val topic: String?
)

fun sendEmailJob(emailJob: EmailJob) {
    val factory = ConnectionFactory().apply { host = "localhost" }
    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->
            val queueName = "emailQ"
            channel.queueDeclare(queueName, true, false, false, null)

            val mapper = jacksonObjectMapper()
            val message = mapper.writeValueAsString(emailJob)

            channel.basicPublish("", queueName, null, message.toByteArray())
            println("✅ Email job queued for ${emailJob.email}")
        }
    }
}

fun fetchNewsArticles(): List<NewsArticle> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://collector-app-573036605406.us-central1.run.app/news")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val type = Types.newParameterizedType(List::class.java, NewsArticle::class.java)
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter<List<NewsArticle>>(type)

        return adapter.fromJson(response.body?.string() ?: "[]") ?: emptyList()
    }
}

fun generateNewsHtml(articles: List<NewsArticle>): String {
    val newsHtml = articles.take(5).joinToString("<hr/>") { article ->
        """
        <h3>${article.title}</h3>
        <p>${article.description}</p>
        <a href="${article.url}">Read more</a>
        <br/><small>${article.publishedAt}</small>
        """.trimIndent()
    }

    return """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2>📰 Your Daily News</h2>
            $newsHtml
            <hr/>
            <p style="font-size: 12px; color: gray;">Sent via Capstone Email Service</p>
        </body>
        </html>
    """.trimIndent()
}

fun fetchNewsArticlesByTopic(topic: String, limit: Int = 3): List<NewsArticle> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://collector-app-573036605406.us-central1.run.app/search?topic=$topic")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Error fetching news: $response")

        val type = Types.newParameterizedType(List::class.java, NewsArticle::class.java)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter<List<NewsArticle>>(type)

        val fullList = adapter.fromJson(response.body?.string() ?: "[]") ?: emptyList()
        return fullList.take(limit) 
    }
}


fun generateCustomNewsHtml(
    generalArticles: List<NewsArticle>,
    subscribedTopics: List<String>
): String {
    val builder = StringBuilder()

    builder.append("<h2>📢 Daily News</h2>")
    builder.append("<h3>📰 Top News</h3><ul>")

    generalArticles.take(3).forEach {
        builder.append("""
            <li>
                <b>${it.title}</b><br/>
                ${it.description ?: "No description"}<br/>
                <a href="${it.url}">Read more</a>
            </li>
        """.trimIndent())
    }
    builder.append("</ul>")

    builder.append("<hr/><h2>🧠 Your Subscribed Topics</h2>")

    for (topic in subscribedTopics) {
        val articles = fetchNewsArticlesByTopic(topic, 3)
        if (articles.isNotEmpty()) {
            builder.append("<h3>🔹 ${topic.capitalize()}</h3><ul>")
            articles.forEach {
                builder.append("""
                    <li>
                        <b>${it.title}</b><br/>
                        ${it.description ?: "No description"}<br/>
                        <a href="${it.url}">Read more</a>
                    </li>
                """.trimIndent())
            }
            builder.append("</ul>")
        }
    }

    builder.append("""
        <hr/>
        <p style="font-size:12px;color:gray;">🧾 This email was generated by your Capstone News Service.</p>
    """.trimIndent())

    return builder.toString()
}






