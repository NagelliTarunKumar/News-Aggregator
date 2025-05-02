package edu.colorado.capstone.collector

import io.mockk.*
import org.junit.jupiter.api.*
import redis.clients.jedis.Jedis
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectorTest {

    private lateinit var redis: Jedis
    private lateinit var collector: Collector
    private val apiKey = "dummy_key"

    @BeforeEach
    fun setup() {
        redis = mockk(relaxed = true)
        collector = Collector(redis)
    }

    @Test
    fun `fetchTopNews returns cached result`() {
        val cachedJson = "[{\"title\": \"Cached News\", \"description\": \"desc\", \"url\": \"https://example.com\", \"publishedAt\": \"2024-01-01T00:00:00Z\"}]"
        every { redis.get("topNews:100") } returns cachedJson

        val result = collector.fetchTopNews(apiKey)

        assertEquals(1, result.size)
        assertEquals("Cached News", result[0].title)
        verify(exactly = 1) { redis.get("topNews:100") }
    }

    @Test
    fun `fetchNewsByTopic returns empty for unknown topic`() {
        val result = collector.fetchNewsByTopic(apiKey, "unknown")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `fetchNewsByQuery returns empty when cache and fetch fail`() {
    every { redis.get(any<String>()) } returns null
    val result = collector.fetchNewsByQuery(apiKey, "nonexistent", 100)
    assertTrue(result.isEmpty())
    }

}
