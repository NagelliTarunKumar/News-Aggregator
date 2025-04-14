package edu.colorado.capstone.email

import com.rabbitmq.client.ConnectionFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.*
import javax.mail.*
import javax.mail.internet.*

fun startConsumer() {
    val factory = ConnectionFactory().apply { host = "localhost" }
    val connection = factory.newConnection()
    val channel = connection.createChannel()

    val queueName = "emailQ"
    channel.queueDeclare(queueName, true, false, false, null)

    val mapper = jacksonObjectMapper()

    val deliverCallback = com.rabbitmq.client.DeliverCallback { _, delivery ->
        val job = mapper.readValue<EmailJob>(String(delivery.body))
        sendEmail(job)
    }

    println("📬 Waiting for email jobs...")
    channel.basicConsume(queueName, true, deliverCallback, { _ -> })
}

fun sendEmail(job: EmailJob) {
    val username = "vishnumahajan33@gmail.com"
    val password = "megz pbsq jbjv wxcw" // ← Gmail App Password

    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
    }

    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication() = PasswordAuthentication(username, password)
    })

    try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(username))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(job.email))
            subject = job.subject
            setText(job.body)
        }
        Transport.send(message)
        println("✅ Email sent to ${job.email}")
    } catch (e: MessagingException) {
        e.printStackTrace()
    }
}
