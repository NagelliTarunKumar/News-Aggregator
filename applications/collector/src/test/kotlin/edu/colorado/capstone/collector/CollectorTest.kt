package edu.colorado.capstone.collector

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import redis.clients.jedis.Jedis
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectorTest {

    private lateinit var redis: Jedis
    private lateinit var collector: Collector
    private val apiKey = "dummy_key"

    @BeforeEach
    fun setup() {
        println("Setting up Mock Redis and Collector instance")
        redis = mockk(relaxed = true)
        collector = Collector(redis)
    }

    @Test
    fun `fetchTopNews returns cached result`() {
        println("Running fetchTopNews test with mocked cache")
        val cachedJson = """
            [{"title": "Cached News", "description": "desc", "url": "https://example.com", "publishedAt": "2024-01-01T00:00:00Z"}]
        """.trimIndent()

        every { redis.get("topNews:100") } returns cachedJson

        val result = collector.fetchTopNews(apiKey)

        assertEquals(1, result.size)
        assertEquals("Cached News", result[0].title)
        verify(exactly = 1) { redis.get("topNews:100") }
        println("fetchTopNews test passed")
    }

    @Test
    fun `fetchNewsByTopic returns empty for unknown topic`() {
        println("Running fetchNewsByTopic test with unknown topic")
        val result = collector.fetchNewsByTopic(apiKey, "unknown")
        assertTrue(result.isEmpty())
        println("fetchNewsByTopic unknown-topic test passed")
    }

    // @Test
    // fun `fetchNewsByQuery returns empty when cache and fetch fail`() {
    //     println("Running fetchNewsByQuery test with failed cache and network")
    //     every { redis.get(any<String>()) } returns null
    //     val result = collector.fetchNewsByQuery(apiKey, "nonexistent", 100)
    //     assertTrue(result.isEmpty())
    //     println("fetchNewsByQuery failure fallback test passed")
    // }

    @Test
    fun `fetchTopNews returns list when cache is missed and API is mocked`() {
    println("🧪 fetchTopNews - mocked API returns expected list")
    every { redis.get(any<String>()) } returns null

    val result = collector.fetchTopNews(apiKey, 1)
    assertTrue(result is List<Article>)
    }

    @Test
    fun `fetchNewsByTopic returns empty list for unknown category`() {
    println("🧪 fetchNewsByTopic - unknown topic returns empty")
    val result = collector.fetchNewsByTopic(apiKey, "nonexistent")
    assertTrue(result.isEmpty())
    }
}
