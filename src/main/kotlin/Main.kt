package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.plugins.*


fun main(args: Array<String>) {
    // The embeddedServer will automatically use configuration from application.conf
    // including port and host specified under ktor.deployment
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureCORS()
    configureStatusPages()
    configureDatabases()
    configureRouting()
}
