plugins {
    kotlin("jvm") version "2.0.0-RC1"
    application
}

group = "edu.colorado.capstone"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.rabbitmq:amqp-client:5.20.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    // For HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // For JSON parsing
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

application {
    mainClass.set("edu.colorado.capstone.email.MainKt")
}
