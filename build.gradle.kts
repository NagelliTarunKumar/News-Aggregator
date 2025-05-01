/* plugins {
    kotlin("jvm") version "2.0.0-RC1"
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
        implementation("com.rabbitmq:amqp-client:5.18.0")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
        implementation("com.sun.mail:javax.mail:1.6.2")


        testImplementation(kotlin("test-junit"))
    }
}*/
plugins {
    kotlin("jvm") version "2.0.0-RC1"
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
        // Use updated versions of dependencies where possible for better memory performance
        implementation("ch.qos.logback:logback-classic:1.5.16")
        implementation("org.slf4j:slf4j-api:2.0.16")
        implementation("org.postgresql:postgresql:42.3.0") // Updated version
        implementation("com.google.cloud.sql:postgres-socket-factory:1.15.0") // Updated version
        implementation("com.rabbitmq:amqp-client:5.20.0") // Updated version
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.5") // Updated version
        implementation("com.sun.mail:javax.mail:1.6.3") // Updated version

        testImplementation(kotlin("test-junit5")) // Using JUnit 5 for better performance
    }
}



