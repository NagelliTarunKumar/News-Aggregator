val postgresVersion: String by project
val hikariVersion: String by project

dependencies {
    implementation ("com.zaxxer:HikariCP:$hikariVersion")

    testImplementation("org.postgresql:postgresql:$postgresVersion")
}
