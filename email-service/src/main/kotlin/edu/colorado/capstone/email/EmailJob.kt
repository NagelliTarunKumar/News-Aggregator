package edu.colorado.capstone.email

data class EmailJob(
    val email: String,
    val subject: String,
    val body: String,
    val topic: String,       // e.g., "technology"
    val htmlContent: String  // full HTML-formatted news block
)
