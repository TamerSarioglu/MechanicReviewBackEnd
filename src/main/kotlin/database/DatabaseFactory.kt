package org.example.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object DatabaseFactory {

    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)
    private val json = Json { prettyPrint = true }

    fun init(config: ApplicationConfig) {
        val env = config.property("ktor.environment").getString()
        val envConfig = config.config("environments.$env")
        val dbConfig = envConfig.config("db")

        val driverClassName = dbConfig.property("driver").getString()
        val jdbcURL = dbConfig.property("url").getString()
        val dbUser = dbConfig.property("user").getString()
        val dbPassword = dbConfig.property("password").getString()
        val poolSize = dbConfig.property("poolSize").getString().toInt()

        logger.info("Initializing database connection for environment: $env")
        logger.info("JDBC URL: $jdbcURL")

        val database = Database.connect(createHikariDataSource(jdbcURL, driverClassName, dbUser, dbPassword, poolSize))

        transaction(database) {
            logger.info("Creating database schema...")
            SchemaUtils.create(UsersTable, MechanicsTable, ReviewsTable)
            logger.info("Database schema created.")

            if (env == "dev") {
                logger.info("Development environment detected. Adding test mechanic...")
                val existingMechanic = MechanicsTable.select { MechanicsTable.name eq "Test Mechanic" }.count()
                if (existingMechanic == 0L) {
                    val mechanicId = UUID.randomUUID().toString()
                    val now = LocalDateTime.now()
                    val nameValue = "Test Mechanic"
                    val addressValue = "123 Test St"
                    val cityValue = "Dev Town"
                    val stateValue = "DV"
                    val zipCodeValue = "12345"
                    val phoneValue = "123-456-7890"
                    val emailValue = "test@mechanic.dev"
                    val websiteValue = "https://test.dev"
                    val specialtiesValue = "General Repairs"
                    val operatingHoursValue = "Mon-Fri 9am-5pm"

                    val mechanicLogData = mapOf(
                        "id" to mechanicId,
                        "name" to nameValue,
                        "address" to addressValue,
                        "city" to cityValue,
                        "state" to stateValue,
                        "zipCode" to zipCodeValue,
                        "phone" to phoneValue,
                        "email" to emailValue,
                        "website" to websiteValue,
                        "specialties" to specialtiesValue,
                        "operatingHours" to operatingHoursValue,
                        "createdAt" to now.toString(),
                        "updatedAt" to now.toString()
                    )

                    MechanicsTable.insert {
                        it[id] = mechanicId
                        it[name] = nameValue
                        it[address] = addressValue
                        it[city] = cityValue
                        it[state] = stateValue
                        it[zipCode] = zipCodeValue
                        it[phone] = phoneValue
                        it[email] = emailValue
                        it[website] = websiteValue
                        it[specialties] = specialtiesValue
                        it[operatingHours] = operatingHoursValue
                        it[createdAt] = now
                        it[updatedAt] = now
                    }

                    try {
                        val mechanicJson = json.encodeToString<Map<String, String>>(mechanicLogData)
                        logger.info("Test mechanic added (JSON):\n$mechanicJson")
                    } catch (e: Exception) {
                        logger.error("Failed to serialize test mechanic data to JSON", e)
                        logger.info("Test mechanic added (Map): $mechanicLogData")
                    }
                } else {
                    logger.info("Test mechanic 'Test Mechanic' already exists.")
                }
            }
        }
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        password: String,
        poolSize: Int
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        username = user
        this.password = password
        maximumPoolSize = poolSize
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}