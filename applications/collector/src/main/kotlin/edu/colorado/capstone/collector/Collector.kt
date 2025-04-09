package edu.colorado.capstone.collector

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("Collector")
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        routing {
            post("/collect") {
                val input = call.receiveText()
                logger.info("Received topic from user: $input")
                call.respondText("Collector received: $input", status = HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}
