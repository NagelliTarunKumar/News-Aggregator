package edu.colorado.capstone.app

import kotlinx.serialization.Serializable

@Serializable
data class NewsItem(
    val title: String,
    val url: String,
    val description: String? = null  // ✅ nullable with default
)
