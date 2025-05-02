group = "edu.colorado.capstone"






val ktorVersion: String by project
val hikariVersion: String by project
val postgresVersion: String by project

dependencies {
    implementation(project(":components:database-support"))

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-freemarker-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    implementation("io.ktor:ktor-server-sessions:$ktorVersion")












    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-gson:2.3.7")

    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.4")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.4:models")




    // Needed for respondText and HTTP status codes
  implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-host-common:2.3.7")
    implementation("io.ktor:ktor-server-call-logging:2.3.7")
    implementation("io.ktor:ktor-server-status-pages:2.3.7")


    

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}

plugins {
    kotlin("jvm") version "2.0.0-RC1"
    application
    kotlin("plugin.serialization") version "2.0.0-RC1"
}

//task<JavaExec>("run") {
//    classpath = files(tasks.jar)
//}

tasks {
    jar {
        manifest { attributes("Main-Class" to "edu.colorado.capstone.app.AppKt") }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map(::zipTree)
        })
    }
}
tasks.withType<Test>().configureEach {
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

/* java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
tasks.test {
    maxHeapSize = "2g"
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
    }
}
*/
// application {
//     mainClass.set("edu.colorado.capstone.app.EmailConsumerKt")
// }

application {
    mainClass.set("edu.colorado.capstone.app.AppKt")










