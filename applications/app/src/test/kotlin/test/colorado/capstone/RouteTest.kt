import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

// Data class for NewsItem
data class NewsItem(
    val title: String,
    val url: String,
    val description: String? = null
)

// Function to categorize news articles
fun categorizeArticles(articles: List<NewsItem>): Map<String, List<NewsItem>> {
    val categorizedArticles = mutableMapOf<String, MutableList<NewsItem>>()

    articles.forEach { article ->
        val category = if (article.title.contains("Google", ignoreCase = true)) {
            "Google"
        } else {
            "Other"
        }

        // Group articles by category
        categorizedArticles.computeIfAbsent(category) { mutableListOf() }.add(article)
    }

    return categorizedArticles
}

// Unit test for categorizing articles
class NewsCategorizerTest {

    @Test
    fun `test categorize articles`() {
        // Mock news articles
        val articles = listOf(
            NewsItem("Google announces new AI feature", "https://google.com/article1"),
            NewsItem("Apple releases new iPhone", "https://apple.com/article2"),
            NewsItem("Google's AI for healthcare", "https://google.com/article3")
        )

        // Call the categorize function
        val categorized = categorizeArticles(articles)

        // Verify Google articles
        val googleArticles = categorized["Google"]
        val otherArticles = categorized["Other"]

        // Check Google category has 2 articles and Other has 1
        assertEquals(2, googleArticles?.size)
        assertEquals(1, otherArticles?.size)

        // Check the titles of the articles in the categories
        assertEquals("Google announces new AI feature", googleArticles?.get(0)?.title)
        assertEquals("Google's AI for healthcare", googleArticles?.get(1)?.title)
        assertEquals("Apple releases new iPhone", otherArticles?.get(0)?.title)
    }
}
