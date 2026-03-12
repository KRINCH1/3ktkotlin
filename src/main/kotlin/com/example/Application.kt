package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AppConfig
import com.example.config.DatabaseConfig
import com.example.exceptions.AppException
import com.example.repositories.TaskRepositoryImpl
import com.example.repositories.UserRepositoryImpl
import com.example.routes.authRoutes
import com.example.routes.taskRoutes
import com.example.services.AuthServiceImpl
import com.example.services.TaskServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Initialize database
    DatabaseConfig.init()
    
    // Configure JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    // Configure CORS
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    
    // Configure JWT authentication
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor-auth-app"
            verifier(
                JWT.require(Algorithm.HMAC256(AppConfig.jwtSecret))
                    .withIssuer(AppConfig.jwtIssuer)
                    .withAudience(AppConfig.jwtAudience)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                if (userId != null) {
                    UserIdPrincipal(userId.toString())
                } else {
                    null
                }
            }
        }
    }
    
    // Configure status pages
    install(StatusPages) {
        exception<AppException> { call, cause ->
            call.respond(HttpStatusCode.fromValue(cause.statusCode), mapOf("error" to cause.message))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }
    
    // Initialize repositories and services
    val userRepository = UserRepositoryImpl()
    val taskRepository = TaskRepositoryImpl()
    val authService = AuthServiceImpl(userRepository)
    val taskService = TaskServiceImpl(taskRepository, userRepository)
    
    // Configure routing
    routing {
        authRoutes(authService)
        taskRoutes(taskService)
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }
    }
}
