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
import com.squareup.moshi.Types
import redis.clients.jedis.Jedis
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Prometheus
import io.prometheus.client.Counter
import io.prometheus.client.hotspot.DefaultExports
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

// Data classes for NewsAPI response
data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val publishedAt: String
)

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

class Collector(private val redis: Jedis) {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi.adapter(NewsResponse::class.java)
    private val articleListAdapter = moshi.adapter<List<Article>>(
        Types.newParameterizedType(List::class.java, Article::class.java)
    )
    private val logger = LoggerFactory.getLogger(Collector::class.java)
    private val cacheTTL: Long = 1800L // 30 minutes

    fun fetchTopNews(apiKey: String, count: Int = 100): List<Article> {
        val cacheKey = "topNews:$count"
        redis.get(cacheKey)?.let { cachedJson ->
            logger.info("Serving top news from Redis cache.")
            return articleListAdapter.fromJson(cachedJson) ?: emptyList()
        }

        val url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=$apiKey"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val body = response.body?.string()
            val news = body
                ?.let { jsonAdapter.fromJson(it) }
                ?.articles
                ?.take(count)
                ?: emptyList()

            val serialized = articleListAdapter.toJson(news)
            redis.setex(cacheKey, cacheTTL, serialized)
            return news
        }

        logger.error("Failed to fetch top news: ${response.message}")
        return emptyList()
    }

    fun fetchNewsByTopic(apiKey: String, topic: String, count: Int = 100): List<Article> {
        val categoryMap = mapOf(
            "finance" to "business",
            "sports" to "sports",
            "fashion" to "entertainment",
            "technology" to "technology",
            "politics" to "general"
        )
        val category = categoryMap[topic.lowercase()] ?: return emptyList()

        val cacheKey = "topicNews:$topic:$count"
        redis.get(cacheKey)?.let { cachedJson ->
            logger.info("Serving news by topic '$topic' from Redis cache.")
            return articleListAdapter.fromJson(cachedJson) ?: emptyList()
        }

        val url =
            "https://newsapi.org/v2/top-headlines?country=us&category=$category&pageSize=$count&apiKey=$apiKey"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val body = response.body?.string()
            val news = body
                ?.let { jsonAdapter.fromJson(it) }
                ?.articles
                ?.take(count)
                ?: emptyList()

            val serialized = articleListAdapter.toJson(news)
            redis.setex(cacheKey, cacheTTL, serialized)
            return news
        }

        logger.error("Failed to fetch news by topic: ${response.message}")
        return emptyList()
    }

    fun fetchNewsByQuery(
        apiKey: String,
        query: String,
        count: Int = 100,
        fromDate: String? = null,
        toDate: String? = null
    ): List<Article> {
        val from = fromDate
            ?: LocalDate.now().minusDays(2).format(DateTimeFormatter.ISO_DATE)
        val to = toDate ?: LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        val cacheKey = "queryNews:$query:$from:$to:$count"

        redis.get(cacheKey)?.let { cachedJson ->
            logger.info("Serving news by query '$query' from Redis cache.")
            return articleListAdapter.fromJson(cachedJson) ?: emptyList()
        }

        val url =
            "https://newsapi.org/v2/everything?q=$query&from=$from&to=$to&apiKey=$apiKey"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val body = response.body?.string()
            val news = body
                ?.let { jsonAdapter.fromJson(it) }
                ?.articles
                ?.take(count)
                ?: emptyList()

            val serialized = articleListAdapter.toJson(news)
            redis.setex(cacheKey, cacheTTL, serialized)
            return news
        }

        logger.error("Failed to fetch news by query: ${response.message}")
        return emptyList()
    }
}

// Prometheus counters
val newsCounter: Counter = Counter.build()
    .name("collector_news_requests_total")
    .help("Total number of /news requests.")
    .register()

val searchCounter: Counter = Counter.build()
    .name("collector_search_requests_total")
    .help("Total number of /search requests.")
    .register()

fun main() {
    val logger = LoggerFactory.getLogger("CollectorServer")
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val apiKey = System.getenv("NEWS_API_KEY") ?: "50333e8541a8449fa59ef605c15d3289"
    val redisHost = System.getenv("REDIS_HOST") ?: "localhost"
    val redisPort = System.getenv("REDIS_PORT")?.toInt() ?: 6379

    val redis = Jedis(redisHost, redisPort)
    val collector = Collector(redis)

    // Register default JVM/system metrics
    DefaultExports.initialize()

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        install(ContentNegotiation) {
            gson { setPrettyPrinting() }
        }

        routing {
         get("/") {
    call.respondText(
        """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>API Documentation - News Collector Service</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    padding: 20px;
                    line-height: 1.6;
                }
                h1, h2 {
                    color: #2c3e50;
                }
                code {
                    background-color: #f4f4f4;
                    padding: 2px 4px;
                    border-radius: 4px;
                    font-family: monospace;
                }
                pre {
                    background-color: #f9f9f9;
                    padding: 10px;
                    border-left: 4px solid #ccc;
                    overflow-x: auto;
                }
            </style>
        </head>
        <body>

        <h1>API Documentation for News Collector Service</h1>

        <h2>1. Introduction</h2>
        <p>
            This document describes the API endpoints for the News Collector service, which is designed to interact with the
            NewsAPI to fetch and search for articles based on various parameters. The service is implemented using Ktor and
            exposes a set of RESTful endpoints for retrieving top headlines, articles by topic, and articles by query.
        </p>

        <h2>2. Service Endpoints</h2>

        <h3>/collect</h3>
        <p><strong>Method:</strong> GET</p>
        <p><strong>Response:</strong></p>
        <pre>{
    "message": "Hi, I am collector"
}</pre>

        <h3>/news</h3>
        <p><strong>Method:</strong> GET</p>
        <p><strong>Response:</strong> List of top news articles</p>
        <pre>[
    {
        "title": "string",
        "description": "string",
        "url": "string",
        "publishedAt": "string"
    }
]</pre>

        <h3>/search</h3>
        <p><strong>Method:</strong> GET</p>
        <p><strong>Query Parameters:</strong></p>
        <ul>
            <li><code>topic</code>: (Optional) ['finance', 'sports', 'fashion', 'technology', 'politics']</li>
            <li><code>q</code>: (Optional) general search term</li>
        </ul>
        <p><strong>Response:</strong></p>
        <pre>[
    {
        "title": "string",
        "description": "string",
        "url": "string",
        "publishedAt": "string"
    }
]</pre>

        <h3>/metrics</h3>
        <p><strong>Method:</strong> GET</p>
        <p><strong>Description:</strong> Exposes JVM and application metrics in Prometheus format for monitoring.</p>
        <p><strong>Example Output:</strong></p>
        <pre>
# HELP jvm_memory_pool_bytes_committed Committed bytes of a given JVM memory pool.
# TYPE jvm_memory_pool_bytes_committed gauge
jvm_memory_pool_bytes_committed{pool="Eden Space",} 6815744.0
        </pre>

        <h2>3. Example Request</h2>
        <pre>GET /search?topic=technology</pre>

        <h2>4. Error Responses</h2>
        <ul>
            <li>400 Bad Request: Missing parameters</li>
            <li>404 Not Found: No articles found</li>
            <li>500 Internal Server Error: Something went wrong</li>
        </ul>

        </body>
        </html>
        """.trimIndent(),
        ContentType.Text.Html
    )
}



            get("/collect") {
                logger.info("GET /collect")
                call.respond(HttpStatusCode.OK, mapOf("message" to "Hi, I am collector"))
            }

            get("/news") {
                newsCounter.inc()
                logger.info("GET /news")
                val articles = collector.fetchTopNews(apiKey)
                if (articles.isNotEmpty()) call.respond(HttpStatusCode.OK, articles)
                else call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to fetch news")
                )
            }

            get("/search") {
                searchCounter.inc()
                val topic = call.request.queryParameters["topic"]
                val query = call.request.queryParameters["q"]

                when {
                    topic != null -> {
                        logger.info("GET /search?topic=$topic")
                        val articles = collector.fetchNewsByTopic(apiKey, topic)
                        if (articles.isNotEmpty()) call.respond(HttpStatusCode.OK, articles)
                        else call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "No articles for topic '$topic'")
                        )
                    }
                    query != null -> {
                        logger.info("GET /search?q=$query")
                        val articles = collector.fetchNewsByQuery(apiKey, query)
                        if (articles.isNotEmpty()) call.respond(HttpStatusCode.OK, articles)
                        else call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to "No articles for query '$query'")
                        )
                    }
                    else -> {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Please provide either 'topic' or 'q'")
                        )
                    }
                }
            }

   get("/metrics") {
    // Ensure the Content-Type header is exactly what Prometheus expects
    call.response.header(HttpHeaders.ContentType, "text/plain;version=0.0.4")
    val writer = java.io.StringWriter()
    TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
    call.respondText(writer.toString())
}


        }
    }.start(wait = true)
}
