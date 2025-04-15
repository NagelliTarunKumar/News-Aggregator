plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "edu.colorado.capstone"
version = "1.0"

application {
    mainClass.set("edu.colorado.capstone.collector.CollectorKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server Core
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-call-logging:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-gson:2.3.7")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // OkHttp for HTTP calls
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Moshi for JSON parsing
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Redis (Jedis)
    implementation("redis.clients:jedis:5.1.0")

    // Testing

}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass.get()
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it) }
        })

        archiveBaseName.set("collector")
        archiveVersion.set("")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
    }
}
