plugins {
    kotlin("jvm") version "2.0.0-RC1"
    application
    kotlin("plugin.serialization") version "1.9.22"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // or 21, just make sure it's consistent and supported
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}
val ktorVersion = "2.3.7"
val coroutinesVersion = "1.7.3"
val serializationVersion = "1.6.3"
val corenlpVersion = "4.5.6"

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Ktor Client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Kotlinx Serialization & Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // Stanford CoreNLP (NER + NLP)
    implementation("edu.stanford.nlp:stanford-corenlp:$corenlpVersion")
    implementation("edu.stanford.nlp:stanford-corenlp:$corenlpVersion:models")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
}


application {
    mainClass.set("edu.colorado.capstone.trending.TrendingKt")
}
