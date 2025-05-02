plugins {
    kotlin("jvm") version "2.0.0-RC1"
    application
}

group = "edu.colorado.capstone"
version = "1.0"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.7"

dependencies {
    // Main app deps
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("redis.clients:jedis:5.1.0")
    implementation("io.prometheus:simpleclient:0.16.0")
    implementation("io.prometheus:simpleclient_common:0.16.0")
    implementation("io.prometheus:simpleclient_hotspot:0.16.0")
    implementation("io.prometheus:simpleclient_httpserver:0.16.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.11.0")

    //  Clean test setup for Kotlin + JUnit 5 (no conflicts)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("io.mockk:mockk:1.13.3")
}

application {
    mainClass.set("edu.colorado.capstone.collector.CollectorKt")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.jar {
    archiveBaseName.set("collector")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map(::zipTree)
    })
}
