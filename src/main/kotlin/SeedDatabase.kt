package org.example

import kotlinx.coroutines.runBlocking
import org.example.database.DatabaseFactory
import org.example.models.Mechanic
import org.example.repositories.MechanicRepository
import kotlin.random.Random

fun main() {
    println("Initializing database...")
    DatabaseFactory.init()
    println("Database initialized.")

    val repository = MechanicRepository()
    val cities = listOf("Springfield", "Shelbyville", "Ogdenville", "North Haverbrook")
    val states = listOf("IL", "OH", "NV", "MA")
    val possibleSpecialties = listOf(
        "Oil Change", "Tire Rotation", "Brake Repair", "Engine Diagnostics",
        "Transmission Service", "Air Conditioning", "Exhaust Systems", "Suspension"
    )

    println("Seeding 10 random mechanics...")

    runBlocking { // Use runBlocking because createMechanic is a suspend function
        repeat(10) { index ->
            val city = cities.random()
            val state = states.random()
            val name = "Random Mechanic #${index + 1} (${city})"
            val numSpecialties = Random.nextInt(1, 4) // 1 to 3 specialties
            val specialties = possibleSpecialties.shuffled().take(numSpecialties)

            val mechanic = Mechanic(
                // id, createdAt, updatedAt are set by createMechanic
                name = name,
                address = "${Random.nextInt(100, 9999)} Random St",
                city = city,
                state = state,
                zipCode = Random.nextInt(10000, 99999).toString(),
                phone = "555-${Random.nextInt(100, 999)}-${Random.nextInt(1000, 9999)}",
                email = "mechanic${index + 1}@${city.lowercase().replace(" ", "")}.com",
                website = "www.random${index + 1}${city.lowercase().replace(" ", "")}.com",
                specialties = specialties,
                operatingHours = "Mon-Fri 9am-5pm" // Keep simple for now
            )

            try {
                val created = repository.createMechanic(mechanic)
                println("Created mechanic: ${created.name} (ID: ${created.id})")
            } catch (e: Exception) {
                 println("Error creating mechanic $name: ${e.message}")
                 // Optionally add more error handling or logging
            }
        }
    }

    println("Finished seeding mechanics.")

    // Log the current mechanics in the database
    println("\n--- Current Mechanics in Database ---")
    runBlocking { // Need runBlocking as searchMechanics is suspend
        try {
            val allMechanics = repository.searchMechanics() // Get all mechanics
            if (allMechanics.isEmpty()) {
                println("No mechanics found in the database.")
            } else {
                allMechanics.forEachIndexed { index, mechanic ->
                    println("${index + 1}. Name: ${mechanic.name}")
                    println("   ID: ${mechanic.id}")
                    println("   Address: ${mechanic.address}, ${mechanic.city}, ${mechanic.state} ${mechanic.zipCode}")
                    println("   Phone: ${mechanic.phone}")
                    println("   Email: ${mechanic.email}")
                    println("   Website: ${mechanic.website}")
                    println("   Specialties: ${mechanic.specialties.joinToString()}")
                    println("   Operating Hours: ${mechanic.operatingHours}")
                    println("   Avg Rating: ${mechanic.averageRating} (${mechanic.totalReviews} reviews)")
                    println("   Created At: ${mechanic.createdAt}")
                    println("   Updated At: ${mechanic.updatedAt}")
                    println("-----")
                }
            }
        } catch (e: Exception) {
            println("Error fetching mechanics from database: ${e.message}")
        }
    }
    println("--- End of Database Log ---")
} 