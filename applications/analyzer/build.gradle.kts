plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "edu.colorado.capstone"
version = "1.0"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.7"

dependencies {
    // Only apply Ktor if building analyzer
    if (projectDir.name == "analyzer") {
        implementation("io.ktor:ktor-server-core:$ktorVersion")
        implementation("io.ktor:ktor-server-netty:$ktorVersion")
        implementation("ch.qos.logback:logback-classic:1.4.11")
    }
}

application {
    // Dynamic main class depending on module
    mainClass.set(
        when (projectDir.name) {
            "analyzer" -> "edu.colorado.capstone.analyzer.AnalyzerKt"
            "collector" -> "edu.colorado.capstone.collector.CollectorKt"
            "app" -> "edu.colorado.capstone.app.AppKt"
            else -> "edu.colorado.capstone.MainKt"
        }
    )
}

tasks {
    jar {
        // Force the output file name
        archiveBaseName.set("analyzer")
        archiveVersion.set("") // This removes the "-1.0" from the filename

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
}
