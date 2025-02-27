package io.initialcapacity.database

import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class DatabaseTemplate(private val dataSource: DataSource) {
    fun <T> execute(sql: String, parameters: (PreparedStatement) -> Unit, results: (ResultSet) -> T): T? {
        return with(sql, parameters) { statement ->
            if (statement.execute() && statement.resultSet.next()) results(statement.resultSet) else null
        }
    }

    fun <T> query(sql: String, parameters: (PreparedStatement) -> Unit, results: (ResultSet) -> T): T? {
        return with(sql, parameters) { statement ->
            statement.executeQuery().use {
                if (it.next()) results(it) else null
            }
        }
    }

    fun <T> queryList(sql: String, parameters: (PreparedStatement) -> Unit, result: (ResultSet) -> T): List<T> {
        return with(sql, parameters) { statement ->
            statement.executeQuery().use {
                val results = mutableListOf<T>()
                while (it.next()) {
                    results.add(result(it))
                }
                results
            }
        }
    }

    private fun <T> with(sql: String, parameters: (PreparedStatement) -> Unit, function: (PreparedStatement) -> T): T {
        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                parameters(statement)
                return function(statement)
            }
        }
    }
}
