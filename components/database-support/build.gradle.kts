val postgresVersion: String by project
val hikariVersion: String by project

dependencies {
    implementation ("com.zaxxer:HikariCP:$hikariVersion")

    testImplementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.14.1")
}

/*java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
    }
}*/
