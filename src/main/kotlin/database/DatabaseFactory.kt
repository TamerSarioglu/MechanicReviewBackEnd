package org.example.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    private val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true
    }

    fun init() {
        // Ensure data directory exists
        File("./data").mkdirs()

        val driverClassName = dotenv["DB_DRIVER"] ?: "org.h2.Driver"
        val jdbcURL = dotenv["DB_URL"] ?: "jdbc:h2:file:./data/mechanic_review_db;DB_CLOSE_DELAY=-1"
        val maxPoolSize = dotenv["DB_MAX_POOL_SIZE"]?.toIntOrNull() ?: 3

        val database = Database.connect(createHikariDataSource(jdbcURL, driverClassName, maxPoolSize))

        transaction(database) {
            SchemaUtils.create(UsersTable, MechanicsTable, ReviewsTable)
        }
    }

    private fun createHikariDataSource(url: String, driver: String, maxPoolSize: Int) =
        HikariDataSource(HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            maximumPoolSize = maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}