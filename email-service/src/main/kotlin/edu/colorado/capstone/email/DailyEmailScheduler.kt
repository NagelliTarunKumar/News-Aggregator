package edu.colorado.capstone.email

import kotlinx.coroutines.*
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.days

fun startDailyEmailScheduler() {
    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            val generalArticles = fetchNewsArticles() // fetches all general news
            val subscribedTopics = listOf("finance", "sports", "fashion", "technology", "politics") 

            val htmlContent = generateCustomNewsHtml(generalArticles, subscribedTopics)

            val emailJob = EmailJob(
                email = "vishnumahajan33@gmail.com",
                subject = "🗞️ Daily News ",
                body = "Here's your daily summary of Top News.",
                topic = "Daily News",
                htmlContent = htmlContent
            )
            sendEmailJob(emailJob)
            delay(1.days)
        }
    }

    // Keep app alive
    println("🟢 Daily scheduler is running. Press Ctrl+C to stop.")
    Thread.currentThread().join()
}
