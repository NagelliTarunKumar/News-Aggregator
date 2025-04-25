package edu.colorado.capstone.app

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.util.CoreMap
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.util.Properties
import edu.colorado.capstone.app.NewsItem


@Serializable
data class Article(
    val title: String,
    val description: String,
    val url: String? = null,
    val publishedAt: String? = null
)


class NewsService {
    private val pipeline = StanfordCoreNLP(Properties().apply {
        setProperty("annotators", "tokenize,ssplit,pos,lemma,ner")
    })

    fun getTopStories(): List<NewsItem> = runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) { json() }
        }

        return@runBlocking try {
            val response: HttpResponse = client.get("https://collector-app-573036605406.us-central1.run.app/news")
            val jsonString = response.bodyAsText()
            val articles = Json { ignoreUnknownKeys = true }
                .decodeFromString<List<NewsItem>>(jsonString)
            articles.take(5)
        } catch (e: Exception) {
            println("Error fetching top stories: ${e.message}")
            emptyList()
        } finally {
            client.close()
        }
    }

    fun getCategorizedNewsForTopic(topic: String): JsonElement = runBlocking {
    val url = "https://collector-app-573036605406.us-central1.run.app/search?topic=$topic"

    val client = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
    }

    return@runBlocking try {
        val response: HttpResponse = client.get(url)
        val jsonString = response.bodyAsText()
        val articlesJson = Json.parseToJsonElement(jsonString).jsonArray

        val articles = articlesJson.mapNotNull { element ->
            val obj = element.jsonObject
            val title = obj["title"]?.jsonPrimitive?.contentOrNull
            val description = obj["description"]?.jsonPrimitive?.contentOrNull
            val url = obj["url"]?.jsonPrimitive?.contentOrNull
            val publishedAt = obj["publishedAt"]?.jsonPrimitive?.contentOrNull

            if (!title.isNullOrEmpty() && !description.isNullOrEmpty()) {
                Article(title, description, url, publishedAt)
            } else null
        }

        val keywordFrequency = mutableMapOf<String, Int>()
        articles.forEach {
            val text = "${it.title}. ${it.description}"
            val entities = extractEntities(text)
            for (entity in entities) {
                keywordFrequency[entity] = keywordFrequency.getOrDefault(entity, 0) + 1
            }
        }

        val topKeywords = keywordFrequency.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }
            .toSet()

        val grouped = mutableMapOf<String, MutableList<Article>>()
        val others = mutableListOf<Article>()

        for (article in articles) {
            val content = "${article.title} ${article.description}".lowercase()
            var matched = false
            for (keyword in topKeywords) {
                if (content.contains(keyword.lowercase())) {
                    grouped.getOrPut("Category=$keyword") { mutableListOf() }.add(article)
                    matched = true
                    break
                }
            }
            if (!matched) others.add(article)
        }

        if (others.isNotEmpty()) {
            grouped["Category=Other Articles"] = others
        }

        // ✅ Limit to 3 categories, each with at most 3 articles
        buildJsonObject {
            grouped.entries
                .sortedByDescending { it.value.size }
                .take(3) // limit categories
                .forEach { (category, articleList) ->
                    put(category, Json.encodeToJsonElement(articleList.take(3))) // limit articles
                }
        }
    } catch (e: Exception) {
        println("Error fetching categorized news: ${e.message}")
        JsonObject(emptyMap())
    } finally {
        client.close()
    }
}


    private fun extractEntities(text: String): List<String> {
        val stopWords = setOf(
            "a", "an", "the", "and", "or", "is", "in", "of", "to", "for", "on", "at", "with", "by",
            "this", "that", "it", "they", "are", "-", ",", ".", "&", "as", "from", "be", "was", "were",
            "news", "sports", "last", "today", "monday", "tuesday", "wednesday", "thursday", "friday",
            "saturday", "sunday", "nbc", "cbs", "abc", "fox", "cnn", "breaking", "alert", "latest", "new"
        )

        val entities = mutableSetOf<String>()
        val doc = pipeline.process(text)
        val sentences: List<CoreMap> = doc.get(CoreAnnotations.SentencesAnnotation::class.java)

        for (sentence in sentences) {
            val tokens = sentence.get(CoreAnnotations.TokensAnnotation::class.java)
            for (token in tokens) {
                val ner = token.get(CoreAnnotations.NamedEntityTagAnnotation::class.java)
                val word = token.word().lowercase().trim()

                if (
                    (ner == "ORGANIZATION" || ner == "PERSON" || ner == "MISC") &&
                    word.length > 2 &&
                    word !in stopWords &&
                    word.matches(Regex("^[a-zA-Z0-9]+$"))
                ) {
                    entities.add(word.replaceFirstChar { it.uppercaseChar() })
                }
            }
        }

        return entities.toList()
    }
}
