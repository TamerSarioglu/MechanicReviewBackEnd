package org.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.models.Mechanic
import org.example.repositories.MechanicRepository

fun Route.mechanicRoutes(mechanicRepository: MechanicRepository) {
    route("/mechanics") {
        authenticate("auth-jwt") {
            post {
                val mechanic = call.receive<Mechanic>()
                val result = mechanicRepository.createMechanic(mechanic)
                call.respond(HttpStatusCode.Created, result)
            }
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("Missing mechanic ID")
            val mechanic = mechanicRepository.getMechanicWithRating(id)
                ?: throw NotFoundException("Mechanic not found")
            call.respond(HttpStatusCode.OK, mechanic)
        }

        get {
            val query = call.request.queryParameters["query"]
            val city = call.request.queryParameters["city"]
            val state = call.request.queryParameters["state"]
            val specialty = call.request.queryParameters["specialty"]

            val mechanics = mechanicRepository.searchMechanics(query, city, state, specialty)
            call.respond(HttpStatusCode.OK, mechanics)
        }
    }
}