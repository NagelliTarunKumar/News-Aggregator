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
}
