package edu.colorado.capstone.app

import io.initialcapacity.database.DatabaseTemplate
import io.ktor.http.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

// Assuming a Session and DB access setup
@Serializable
data class UserSession(val email: String)

fun Routing.index(databaseTemplate: DatabaseTemplate) {
    get("/") {
        // Check if user is logged in by verifying the session
        val userSession = call.sessions.get<UserSession>()
        if (userSession != null) {
            // User is logged in
            call.respond("Welcome user with ID: ${userSession.email}")
//            call.respond(FreeMarkerContent("index.ftl", emptyMap<String, String>()))
        } else {
            // User is not logged in
            call.respondRedirect("/register")
        }
    }

    get("/login") {
        // TODO uncomment when tests work
//        if (call.sessions.get<UserSession>() != null) {
//            call.respondRedirect("/")
//        }

        call.respond(FreeMarkerContent("login.ftl", emptyMap<String, String>()))
    }

    post("/login") {
        val params = call.receiveParameters()
        val email = params["email"]

        if (email != null) {
            val userExists = databaseTemplate.query(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                parameters = { stmt -> stmt.setString(1, email) },
                results = { rs -> rs.getInt(1) > 0 }
            ) ?: false

            if (userExists) {
                call.sessions.set(UserSession(email))
                call.respondRedirect("/")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "User not found.")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Missing email.")
        }
    }

    get("register") {
        // TODO uncomment when tests work
//        if (call.sessions.get<UserSession>() != null) {
//            call.respondRedirect("/")
//        }

        call.respond(FreeMarkerContent("register.ftl", emptyMap<String, String>()))
    }

    post("/register") {
        val params = call.receiveParameters()
        val email = params["email"]



        if (email != null) {
            val userExists = databaseTemplate.query(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                parameters = { stmt -> stmt.setString(1, email) },
                results = { rs -> rs.getInt(1) > 0 }
            ) ?: false

            if (!userExists) {
                // Insert new user
                databaseTemplate.execute(
                    "INSERT INTO users (email) VALUES (?)",
                    parameters = { stmt ->
                        stmt.setString(1, email)
                    },
                    results = { it -> true } // Just return true to indicate success
                )
                call.sessions.set(UserSession(email))
                call.respondRedirect("/")
            }
            else {
                call.respond(HttpStatusCode.BadRequest, "Email already exists.")
                call.respondRedirect("/register")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Missing email.")
        }
    }

    get("/logout") {
        call.sessions.clear<UserSession>()
        call.respondRedirect("/login")
    }
}
