package edu.colorado.capstone.email

fun main(args: Array<String>) {
    if (args.contains("consumer")) {
        println("📬 Starting RabbitMQ consumer...")
        startConsumer()
    } else {
        println("⏰ Starting daily scheduler...")
        startDailyEmailScheduler()
    }
}