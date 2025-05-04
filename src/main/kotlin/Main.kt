package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import org.example.plugins.*
import org.example.repositories.MechanicRepository


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureCORS()
    configureStatusPages()
    configureValidation()
    configureDatabases()

    val mechanicRepository = MechanicRepository()
    log.info("--- Logging Mechanics from Database at Startup ---")
    runBlocking {
        try {
            val allMechanics = mechanicRepository.searchMechanics()
            if (allMechanics.isEmpty()) {
                log.info("No mechanics found in the database.")
            } else {
                log.info("Found ${allMechanics.size} mechanics:")
                allMechanics.forEach { mechanic ->
                    log.info("  - Name: ${mechanic.name}, City: ${mechanic.city}, State: ${mechanic.state}, Avg Rating: ${mechanic.averageRating} (${mechanic.totalReviews} reviews)")
                }
            }
        } catch (e: Exception) {
            log.error("Error fetching mechanics from database during startup: ${e.message}", e)
        }
    }
    log.info("--- Finished Logging Mechanics ---")

    configureRouting()
}
