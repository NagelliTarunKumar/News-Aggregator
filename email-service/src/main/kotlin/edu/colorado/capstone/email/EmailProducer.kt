package edu.colorado.capstone.email

import com.rabbitmq.client.ConnectionFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun sendEmailJob(emailJob: EmailJob) {
    val factory = ConnectionFactory().apply { host = "localhost" }
    factory.newConnection().use { connection ->
        connection.createChannel().use { channel ->
            val queueName = "emailQ"
            channel.queueDeclare(queueName, true, false, false, null)

            val mapper = jacksonObjectMapper()
            val message = mapper.writeValueAsString(emailJob)

            channel.basicPublish("", queueName, null, message.toByteArray())
            println("✅ Email job queued for ${emailJob.email}")
        }
    }
}
