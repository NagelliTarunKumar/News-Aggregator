package edu.colorado.capstone.app

import freemarker.cache.ClassTemplateLoader
import io.initialcapacity.database.DatabaseTemplate
import io.initialcapacity.database.dataSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun Application.configured(
    databaseUrl: String,
) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val dataSource = dataSource(databaseUrl)
    val databaseTemplate = DatabaseTemplate(dataSource)

    routing {

     
        index(databaseTemplate)
        health(databaseTemplate)

        staticResources("/static/styles", "static/styles")
        staticResources("/static/images", "static/images")
            get("/analyse") {
    
    call.respondText("Hi, I am the App along the analyser logic", status = HttpStatusCode.OK)
}
            get("/categorise") {
            val topic = call.request.queryParameters["topic"]
            
            if (topic != null) {
                val newsService = NewsService() // Initialize the service
                val categorizedNews = newsService.getCategorizedNewsForTopic(topic)

                // Return the response with the categorized news
                call.respond(HttpStatusCode.OK, categorizedNews)
            } else {
                // If topic query parameter is not provided, return BadRequest
                call.respond(HttpStatusCode.BadRequest, "Topic query parameter is required")
            }
        }
    }
}

fun Application.module() {
    val databaseUrl = requiredEnvironmentVariable("DATABASE_URL")

    configured(databaseUrl)

    install(Sessions) {
        cookie<UserSession>("user_session")
    }
}

fun main() {
    val port = optionalEnvironmentVariable("PORT", "8082")
    embeddedServer(Netty, port = port.toInt(), host = "0.0.0.0", module = { module() })
        .start(wait = true)
}
