package edu.colorado.capstone.app

import io.initialcapacity.database.DatabaseTemplate
import io.ktor.http.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import javax.annotation.Nonnull

// Assuming a Session and DB access setup
@Serializable
data class UserSession(@Nonnull val email: String)

fun Routing.index(databaseTemplate: DatabaseTemplate) {
    get("/") {
        // Check if user is logged in by verifying the session
        val userSession = call.sessions.get<UserSession>()
        if (userSession != null) {
            // User is logged in

            // get user's news subscriptions
            val userData: Map<String, Any?>? = databaseTemplate.query(
                "SELECT * FROM users WHERE email = ?",
                parameters = { stmt -> stmt.setString(1, userSession.email) },
                results = { rs ->
                    val meta = rs.metaData
                    val columnCount = meta.columnCount
                    buildMap {
                        for (i in 4..columnCount) {
                            val columnName = meta.getColumnLabel(i)
                            put(columnName, rs.getBoolean(i))
                        }
                    }
                }
            )

            call.respond(FreeMarkerContent(
                "index.ftl",
                mapOf("email" to userSession.email,
                    "finance" to userData?.get("finance"),
                    "sports" to userData?.get("sports"),
                    "fashion" to userData?.get("fashion"),
                    "technology" to userData?.get("technology"),
                    "politics" to userData?.get("politics"))))
//            call.respond("Welcome user with ID: ${userSession.email}")
//            call.respond(FreeMarkerContent("index.ftl", emptyMap<String, String>()))
        } else {
            // User is not logged in
            call.respondRedirect("/register")
        }
    }

    post("/update") {
        val params = call.receiveParameters()
        val finance = params["finance"] != null
        val sports = params["sports"] != null
        val fashion = params["fashion"] != null
        val technology = params["technology"] != null
        val politics = params["politics"] != null

        // Insert new user
        databaseTemplate.execute(
            "UPDATE users SET finance = ?, sports = ?, fashion = ?, technology = ?, politics = ? WHERE email = ?",
            parameters = { stmt ->
                stmt.setBoolean(1, finance)
                stmt.setBoolean(2, sports)
                stmt.setBoolean(3, fashion)
                stmt.setBoolean(4, technology)
                stmt.setBoolean(5, politics)
                stmt.setString(6, call.sessions.get<UserSession>()!!.email)
            },
            results = { it -> true } // Just return true to indicate success
        )

        call.respondRedirect("/")
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
