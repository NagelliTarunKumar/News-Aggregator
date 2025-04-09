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
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.4.11")
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
