package edu.colorado.capstone.app

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk

class NewsItemTest {

    @Test
    fun `test creating NewsItem with all properties`() {
        // Create a NewsItem with all fields (title, url, description)
        val newsItem = NewsItem(
            title = "Breaking News: Kotlin is Awesome!",
            url = "https://kotlinlang.org",
            description = "Kotlin is a great language for modern development."
        )

        // Assert that the NewsItem has the correct properties
        assertEquals("Breaking News: Kotlin is Awesome!", newsItem.title)
        assertEquals("https://kotlinlang.org", newsItem.url)
        assertEquals("Kotlin is a great language for modern development.", newsItem.description)
    }

    @Test
    fun `test creating NewsItem with only title and url`() {
        // Create a NewsItem with just title and url (description should be null)
        val newsItem = NewsItem(
            title = "Kotlin 1.6 Released",
            url = "https://kotlinlang.org/blog/2021/12/01/kotlin-1-6-0-released"
        )

        // Assert that the NewsItem has the correct properties
        assertEquals("Kotlin 1.6 Released", newsItem.title)
        assertEquals("https://kotlinlang.org/blog/2021/12/01/kotlin-1-6-0-released", newsItem.url)
        assertNull(newsItem.description)  // Since description is nullable and not provided
    }

    @Test
    fun `test serialization of NewsItem`() {
        // Create a NewsItem
        val newsItem = NewsItem(
            title = "Kotlin Serialization",
            url = "https://kotlinlang.org/docs/serialization.html",
            description = "Learn how to use Kotlin serialization to work with JSON and other formats."
        )

        // Serialize the NewsItem to JSON string
        val jsonString = Json.encodeToString(newsItem)

        // Assert that the JSON string contains the expected fields
        assertTrue(jsonString.contains("\"title\":\"Kotlin Serialization\""))
        assertTrue(jsonString.contains("\"url\":\"https://kotlinlang.org/docs/serialization.html\""))
        assertTrue(jsonString.contains("\"description\":\"Learn how to use Kotlin serialization to work with JSON and other formats.\""))
    }

    @Test
    fun `test deserialization of NewsItem`() {
        // Define a JSON string representing a NewsItem
        val jsonString = """
            {
                "title": "Kotlin 1.6.0 Features",
                "url": "https://kotlinlang.org/docs/whatsnew/1.6.0.html",
                "description": "Kotlin 1.6.0 introduces many new features."
            }
        """.trimIndent()

        // Deserialize the JSON string into a NewsItem object
        val newsItem = Json.decodeFromString<NewsItem>(jsonString)

        // Assert that the deserialized object has the correct properties
        assertEquals("Kotlin 1.6.0 Features", newsItem.title)
        assertEquals("https://kotlinlang.org/docs/whatsnew/1.6.0.html", newsItem.url)
        assertEquals("Kotlin 1.6.0 introduces many new features.", newsItem.description)
    }

    @Test
    fun `test mock NewsItem usage`() {
        // Mock a NewsItem using MockK
        val mockNewsItem = mockk<NewsItem>()

        // Define behavior of the mock
        every { mockNewsItem.title } returns "Mock News"
        every { mockNewsItem.url } returns "https://mocknews.com"
        every { mockNewsItem.description } returns "This is a mock description."

        // Assert that the mock returns the correct values
        assertEquals("Mock News", mockNewsItem.title)
        assertEquals("https://mocknews.com", mockNewsItem.url)
        assertEquals("This is a mock description.", mockNewsItem.description)
    }
}
