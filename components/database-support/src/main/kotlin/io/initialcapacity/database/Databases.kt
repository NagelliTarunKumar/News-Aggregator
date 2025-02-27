package io.initialcapacity.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun dataSource(url: String): DataSource = HikariDataSource(HikariConfig().apply {
    jdbcUrl = url
    validate()
})
