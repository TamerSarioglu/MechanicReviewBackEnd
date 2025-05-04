package org.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.github.cdimascio.dotenv.dotenv

fun Application.configureCORS() {
    val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true
    }
    
    // Get allowed hosts from environment or use defaults for local development
    val allowedHosts = dotenv["CORS_ALLOWED_HOSTS"]?.split(",") ?: 
        listOf("localhost:3000", "localhost:8080", "10.0.2.2:8080", "10.0.2.2:3000")
    
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
        maxAgeInSeconds = 3600
        
        // Use a simplified approach for localhost development
        allowHost("localhost:3000")
        allowHost("localhost:8080")
        allowHost("10.0.2.2:8080")
        allowHost("10.0.2.2:3000")
        
        // Also allow any custom hosts from the environment
        allowedHosts.filter { 
            it != "localhost:3000" && 
            it != "localhost:8080" && 
            it != "10.0.2.2:8080" && 
            it != "10.0.2.2:3000" 
        }.forEach { 
            allowHost(it) 
        }
    }
}