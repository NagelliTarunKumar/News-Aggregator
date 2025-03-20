val postgresVersion: String by project
val hikariVersion: String by project

dependencies {
    implementation ("com.zaxxer:HikariCP:$hikariVersion")

    testImplementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.14.1")
}
