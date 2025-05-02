package edu.colorado.capstone.analyzer

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import org.slf4j.LoggerFactory

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8081
    val logger = LoggerFactory.getLogger("Analyzer")

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        routing {
            get("/") {
    logger.info("GET request received at /analyze")
    call.respondText("Hi, I am Analyzer the APP directly my logic in it", status = HttpStatusCode.OK)
}
            post("/analyze") {
                val input = call.receiveText()
                logger.info("Received: $input")
                call.respondText("Analyzing: $input", status = HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}
