package edu.colorado.capstone.email

import kotlinx.coroutines.*
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.days

fun startDailyEmailScheduler() {
    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            val job = EmailJob(
                email = "vishnumahajan33@gmail.com",
                subject = "🕒 Scheduled Capstone Email",
                body = "This is your daily update at ${LocalDateTime.now()}!"
            )
            sendEmailJob(job)
            delay(1.days)
        }
    }

    // Keep app alive
    println("🟢 Daily scheduler is running. Press Ctrl+C to stop.")
    Thread.currentThread().join()
}
