package edu.colorado.capstone.app

import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.index() {
    get("/") {
        call.respond(FreeMarkerContent("index.ftl", emptyMap<String, String>()))
    }
}
