package edu.colorado.capstone.app

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class NewsItem(
    val title: String,
    val url: String,
    val description: String? = null
)

fun categorizeArticles(articles: List<NewsItem>): Map<String, List<NewsItem>> {
    val categorized = mutableMapOf<String, MutableList<NewsItem>>()
    for (article in articles) {
        val category = if ("Google" in article.title) "Google" else "Other"
        categorized.computeIfAbsent(category) { mutableListOf() }.add(article)
    }
    return categorized
}

class RouteTest {

    @Test
    fun testCategorizeArticlesReturnsCorrectGrouping() {
        println("✅ Running categorizeArticles test")

        val articles = listOf(
            NewsItem("Google launches AI", "https://google.com/ai"),
            NewsItem("Apple unveils iPhone", "https://apple.com/iphone"),
            NewsItem("Google Cloud expansion", "https://google.com/cloud")
        )

        val categorized = categorizeArticles(articles)

        println("Categorized result: $categorized")

        assertEquals(2, categorized["Google"]?.size)
        assertEquals(1, categorized["Other"]?.size)

        assertTrue(categorized["Google"]!!.any { "AI" in it.title })
        assertTrue(categorized["Other"]!!.all { "Google" !in it.title })

        println("✅ categorizeArticles test passed")
    }

    @Test
    fun testCategorizeArticlesWithEmptyList() {
        val articles = emptyList<NewsItem>()
        val categorized = categorizeArticles(articles)

        assertTrue(categorized.isEmpty())
    }

    @Test
    fun testAllArticlesInGoogleCategory() {
        val articles = listOf(
            NewsItem("Google News One", "https://google.com/1"),
            NewsItem("Google News Two", "https://google.com/2")
        )
        val categorized = categorizeArticles(articles)

        assertEquals(2, categorized["Google"]?.size)
        assertTrue("Other" !in categorized)
    }

    @Test
    fun testNoGoogleArticles() {
        val articles = listOf(
            NewsItem("Apple introduces M3", "https://apple.com"),
            NewsItem("Microsoft builds AI tools", "https://microsoft.com")
        )
        val categorized = categorizeArticles(articles)

        assertEquals(2, categorized["Other"]?.size)
        assertTrue("Google" !in categorized)
    }

    @Test
    fun testCaseSensitivityInCategorization() {
        val articles = listOf(
            NewsItem("google announces search changes", "https://google.com") // lowercase "google"
        )
        val categorized = categorizeArticles(articles)

        assertEquals(1, categorized["Other"]?.size)
        assertTrue("Google" !in categorized)
    }
}
