package edu.colorado.capstone.collector

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import org.slf4j.LoggerFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data classes for NewsAPI response
data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val publishedAt: String // Added the publishedAt field
)

data class NewsResponse(val status: String, val totalResults: Int, val articles: List<Article>)

class Collector {

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val jsonAdapter = moshi.adapter(NewsResponse::class.java)
    private val logger = LoggerFactory.getLogger(Collector::class.java)

    // Fetch top news from NewsAPI
    fun fetchTopNews(apiKey: String, count: Int = 100): List<Article> {
        val url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=$apiKey"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                val newsResponse = jsonAdapter.fromJson(responseBody)
                return newsResponse?.articles?.take(count) ?: emptyList()
            } else {
                logger.error("No response body found.")
            }
        } else {
            logger.error("Failed to fetch news: ${response.message}")
        }
        return emptyList()
    }

    // Fetch news by topic (category)
    fun fetchNewsByTopic(apiKey: String, topic: String, count: Int = 100
    ): List<Article> {
        val categoryMap = mapOf(
            "finance" to "business",
            "sports" to "sports",
            "fashion" to "entertainment",
            "technology" to "technology",
            "politics" to "general"
        )

        val category = categoryMap[topic.lowercase()]
        if (category == null) {
            logger.error("Invalid topic")
            return emptyList()
        }

        val url = "https://newsapi.org/v2/top-headlines?country=us&category=$category&pageSize=$count&apiKey=$apiKey"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                val newsResponse = jsonAdapter.fromJson(responseBody)
                return newsResponse?.articles?.take(count) ?: emptyList()
            } else {
                logger.error("No response body found.")
            }
        } else {
            logger.error("Failed to fetch news by topic: ${response.message}")
        }
        return emptyList()
    }

    // Fetch news by query (search articles)
    fun fetchNewsByQuery(apiKey: String, query: String, count: Int = 100, fromDate: String? = null, toDate: String? = null): List<Article> {
    // Calculate default date range if not provided
    val from = fromDate ?: LocalDate.now().minusDays(2).format(DateTimeFormatter.ISO_DATE)
    val to = toDate ?: LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    
    val url = "https://newsapi.org/v2/everything?q=$query&from=$from&to=$to&apiKey=$apiKey"
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()

    if (response.isSuccessful) {
        val responseBody = response.body?.string()
        if (responseBody != null) {
            val newsResponse = jsonAdapter.fromJson(responseBody)
            return newsResponse?.articles?.take(count) ?: emptyList()
        } else {
            logger.error("No response body found.")
        }
    } else {
        logger.error("Failed to fetch news by query: ${response.message}")
    }
    return emptyList()
}
}

fun main() {
    val logger = LoggerFactory.getLogger("CollectorServer")
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val apiKey = "50333e8541a8449fa59ef605c15d3289" // Replace with your actual NewsAPI key
    val collector = Collector()

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        routing {
            // Basic collect endpoint
            get("/collect") {
                logger.info("GET request received at /collect")
                call.respond(HttpStatusCode.OK, mapOf("message" to "Hi, I am collector"))
            }

            // Fetch top news
            get("/news") {
                logger.info("Fetching top news...")
                val articles = collector.fetchTopNews(apiKey)
                if (articles.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, articles)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to fetch news or no news available."))
                }
            }

            // Search by topic or query
            get("/search") {
                val topic = call.request.queryParameters["topic"]
                val query = call.request.queryParameters["q"]

                when {
                    topic != null -> {
                        logger.info("Fetching news for topic: $topic")
                        val articles = collector.fetchNewsByTopic(apiKey, topic)
                        if (articles.isNotEmpty()) {
                            call.respond(HttpStatusCode.OK, articles)
                        } else {
                            call.respond(HttpStatusCode.NotFound, mapOf("error" to "No articles found for topic '$topic'."))
                        }
                    }

                    query != null -> {
                        logger.info("Searching for query: $query")
                        val articles = collector.fetchNewsByQuery(apiKey, query)
                        if (articles.isNotEmpty()) {
                            call.respond(HttpStatusCode.OK, articles)
                        } else {
                            call.respond(HttpStatusCode.NotFound, mapOf("error" to "No articles found for query '$query'."))
                        }
                    }

                    else -> {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Please provide either a 'topic' or 'q' parameter for search.")
                        )
                    }
                }
            }
        }
    }.start(wait = true)
}
