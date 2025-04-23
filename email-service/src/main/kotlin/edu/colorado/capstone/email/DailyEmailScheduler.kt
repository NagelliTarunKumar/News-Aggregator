package edu.colorado.capstone.email

import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.time.Duration.Companion.days

// import your job and mail helpers as needed:
// import edu.colorado.capstone.email.EmailJob
// import edu.colorado.capstone.email.fetchNewsArticles
// import edu.colorado.capstone.email.generateCustomNewsHtml
// import edu.colorado.capstone.email.sendEmailJob

data class UserPrefs(val email: String, val topics: List<String>)

// 1. JDBC connection using your Cloud SQL URL
fun getDatabaseConnection(): Connection {
    val url = "jdbc:postgresql:///starter_development?cloudSqlInstance=s25-fse-team9:us-central1:gcp-cloud-starter&socketFactory=com.google.cloud.sql.postgres.SocketFactory&user=starter&password=starter"
    return DriverManager.getConnection(url)
}

// 2. Fetch users and topic preferences with logging
fun fetchAllUsersWithPreferences(): List<UserPrefs> {
    val topicsList = listOf("finance", "sports", "fashion", "technology", "politics")
    val users = mutableListOf<UserPrefs>()
    val conn = getDatabaseConnection()
    try {
        val stmt = conn.prepareStatement("SELECT * FROM users")
        val rs = stmt.executeQuery()
        while (rs.next()) {
            val email = rs.getString("email")
            val topics = topicsList.filter { topic ->
                val value = rs.getObject(topic)
                value == true || value == "t"
            }
            println("🔎 Found user: $email with topics: $topics")
            users.add(UserPrefs(email, topics))
        }
        rs.close()
        stmt.close()
    } finally {
        conn.close()
    }
    println("👥 Total users found: ${users.size}")
    return users
}

// 3. Main daily email scheduler with per-user diagnostics
fun startDailyEmailScheduler() {
    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            println("⏰ Starting daily email scheduler loop...")
            val users = fetchAllUsersWithPreferences()
            val generalArticles = fetchNewsArticles()
            println("📰 News articles fetched: ${generalArticles.size}")

            for (user in users) {
                println("➡️ Processing user: ${user.email} with topics: ${user.topics}")
                if (user.topics.isEmpty()) {
                    println("⚠️ Skipping ${user.email}: No topics selected")
                    continue
                }
                try {
                    val htmlContent = generateCustomNewsHtml(generalArticles, user.topics)
                    val emailJob = EmailJob(
                        email = user.email,
                        subject = "🗞️ Daily News",
                        body = "Here's your daily summary of Top News.",
                        topic = "Daily News",
                        htmlContent = htmlContent
                    )
                    println("📧 Enqueuing email job for ${user.email}...")
                    sendEmailJob(emailJob)
                    println("✅ Email job enqueued for ${user.email}")
                } catch (e: Exception) {
                    println("❌ Error for ${user.email}: ${e.message}")
                    e.printStackTrace()
                }
            }

            println("⏳ All emails enqueued. Waiting 24 hours...")
            delay(1.days)
        }
    }

    println("🟢 Daily scheduler is running. Press Ctrl+C to stop.")
    Thread.currentThread().join()
}
