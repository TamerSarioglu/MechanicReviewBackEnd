package org.example.plugins

import io.ktor.server.application.*
import org.example.database.DatabaseFactory

fun Application.configureDatabases() {
    DatabaseFactory.init(environment.config)
}