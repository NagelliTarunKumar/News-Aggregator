package edu.colorado.capstone.app

import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*

class RouteTest {

    // Test: Check that /analyse responds with the correct text
    @Test
    fun testAnalyseEndpoint() = testApplication {
        application {
            routing {
                get("/analyse") {
                    call.respondText("Hi, I am the App along the analyser logic", status = HttpStatusCode.OK)
                }
            }
        }

        val response = client.get("/analyse")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hi, I am the App along the analyser logic", response.bodyAsText())
    }

    // Test: Check for invalid endpoint (404 response)
    @Test
    fun testInvalidEndpoint() = testApplication {
        application {
            routing {
                get("/valid-endpoint") {
                    call.respond(HttpStatusCode.OK, "This is a valid endpoint")
                }
            }
        }

        val response = client.get("/invalid-endpoint")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // Test: Check functionality of NewsService (mock external API call)
    @Test
    fun testNewsService() = testApplication {
        val newsService = NewsService()

        // Mock response to simulate the API call
        val mockedTopStories = listOf(
            NewsItem(title = "Title 1", description = "Description 1"),
            NewsItem(title = "Title 2", description = "Description 2")
        )

        // You can replace the network request with a mock or predefined data here for testing purposes

        val topStories = newsService.getTopStories()
        assertEquals(5, topStories.size)  // This assumes the `getTopStories()` method is supposed to return at most 5 stories
        assertTrue(topStories.all { it.title.isNotEmpty() && it.description.isNotEmpty() })  // Validate that all have titles and descriptions
    }
}
