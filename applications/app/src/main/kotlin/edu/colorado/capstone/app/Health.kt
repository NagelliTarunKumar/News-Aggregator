package edu.colorado.capstone.app

import io.initialcapacity.database.DatabaseTemplate
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.health(databaseTemplate: DatabaseTemplate) {
    get("/health") {
        val result = databaseTemplate.query("select 1", {}, { resultSet -> resultSet.getInt(1) })
        if (result == 1) {
            call.respondText("""{"status": "up"}""", ContentType.Application.Json, HttpStatusCode.OK)
        } else {
            call.respondText("""{"status": "down"}""", ContentType.Application.Json, HttpStatusCode.InternalServerError)
        }
    }
}
