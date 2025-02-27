package edu.colorado.capstone.app

fun optionalEnvironmentVariable(value: String, default: String): String {
    return System.getenv()[value] ?: default
}

fun requiredEnvironmentVariable(value: String): String {
    return System.getenv()[value] ?: throw RuntimeException("missing configuration: $value")
}
