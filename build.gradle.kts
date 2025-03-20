plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

subprojects {
    if (name == "applications" || name == "components") return@subprojects

    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("ch.qos.logback:logback-classic:1.5.16")
        implementation("org.slf4j:slf4j-api:2.0.16")
        implementation ("org.postgresql:postgresql:42.2.5")
        implementation("com.google.cloud.sql:postgres-socket-factory:1.14.1")

        testImplementation(kotlin("test-junit"))
    }
}
