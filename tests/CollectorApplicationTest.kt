package edu.colorado.capstone.collector

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Use the same data classes from your production code.
data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val publishedAt: String
)

// Define an inline test module that mimics the production routing
fun Application.testModule() {
    // A dummy collector object that returns a fixed article list.
    val testArticle = Article(
        title = "Test Title",
        description = "Test Description",
        url = "http://example.com",
        publishedAt = "2025-04-14T00:00:00Z"
    )
    val testCollector = object {
        fun fetchTopNews(apiKey: String, count: Int = 100): List<Article> = listOf(testArticle)
        fun fetchNewsByTopic(apiKey: String, topic: String, count: Int = 100): List<Article> = listOf(testArticle)
        fun fetchNewsByQuery(
            apiKey: String,
            query: String,
            count: Int = 100,
            fromDate: String? = null,
            toDate: String? = null
        ): List<Article> = listOf(testArticle)
    }

    // We install content negotiation for JSON responses.
    install(ContentNegotiation) {
        gson { setPrettyPrinting() }
    }

    // Here is the routing, which closely follows your production server.
    routing {
        get("/") {
            // Return the HTML documentation.
            val html = """
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
            """.trimIndent()
            call.respondText(html, ContentType.Text.Html)
        }

        get("/collect") {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Hi, I am collector"))
        }

        get("/news") {
            // For tests we use a dummy API key.
            val apiKey = "dummyApiKey"
            val articles = testCollector.fetchTopNews(apiKey)
            if (articles.isNotEmpty()) call.respond(HttpStatusCode.OK, articles)
            else call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Failed to fetch news")
            )
        }

        get("/search") {
            val topic = call.request.queryParameters["topic"]
            val query = call.request.queryParameters["q"]
            val apiKey = "dummyApiKey"

            when {
                topic != null -> {
                    val articles = testCollector.fetchNewsByTopic(apiKey, topic)
                    if (articles.isNotEmpty()) call.respond(HttpStatusCode.OK, articles)
                    else call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "No articles for topic '$topic'")
                    )
                }
                query != null -> {
                    val articles = testCollector.fetchNewsByQuery(apiKey, query)
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
    }
}

class CollectorApplicationTest {

    @Test
    fun testRootEndpointReturnsHtml() = testApplication {
        application { testModule() }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val contentType = response.headers[HttpHeaders.ContentType] ?: ""
        // Verify that the Content-Type header is HTML.
        assertTrue { contentType.contains(ContentType.Text.Html.contentType) }
        // Check that the response contains the "<!DOCTYPE html>" declaration.
        assertTrue(response.bodyAsText().contains("<!DOCTYPE html>"))
    }

    @Test
    fun testCollectEndpoint() = testApplication {
        application { testModule() }
        val response = client.get("/collect")
        assertEquals(HttpStatusCode.OK, response.status)
        // Expect JSON with message "Hi, I am collector"
        assertTrue(response.bodyAsText().contains("Hi, I am collector"))
    }

    @Test
    fun testNewsEndpointReturnsArticles() = testApplication {
        application { testModule() }
        val response = client.get("/news")
        assertEquals(HttpStatusCode.OK, response.status)
        // Since our dummy collector returns one article, check that we get that article title.
        assertTrue(response.bodyAsText().contains("Test Title"))
    }

    @Test
    fun testSearchEndpointWithTopic() = testApplication {
        application { testModule() }
        // Call /search endpoint with a "topic" parameter.
        val response = client.get("/search?topic=technology")
        assertEquals(HttpStatusCode.OK, response.status)
        // Verify the returned article matches the dummy article.
        assertTrue(response.bodyAsText().contains("Test Title"))
    }

    @Test
    fun testSearchEndpointWithQuery() = testApplication {
        application { testModule() }
        // Call /search endpoint with a "q" parameter.
        val response = client.get("/search?q=bitcoin")
        assertEquals(HttpStatusCode.OK, response.status)
        // Verify dummy data is returned.
        assertTrue(response.bodyAsText().contains("Test Title"))
    }

    @Test
    fun testSearchEndpointBadRequest() = testApplication {
        application { testModule() }
        // Call /search endpoint without any query parameters.
        val response = client.get("/search")
        // Expect a 400 Bad Request response.
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Please provide either 'topic' or 'q'"))
    }
}
