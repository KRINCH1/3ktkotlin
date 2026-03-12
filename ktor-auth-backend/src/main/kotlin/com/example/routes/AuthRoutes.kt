package com.example.routes

import com.example.exceptions.AppException
import com.example.models.UserCreateRequest
import com.example.models.UserLoginRequest
import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.authRoutes(authService: AuthService) {
    val logger = LoggerFactory.getLogger("AuthRoutes")
    
    route("/api/auth") {
        post("/register") {
            try {
                val request = call.receive<UserCreateRequest>()
                val response = authService.register(request)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: AppException) {
                call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
            } catch (e: Exception) {
                logger.error("Registration error", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
            }
        }
        
        post("/login") {
            try {
                val request = call.receive<UserLoginRequest>()
                val response = authService.login(request)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: AppException) {
                call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
            } catch (e: Exception) {
                logger.error("Login error", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
            }
        }
        
        authenticate("auth-jwt") {
            get("/me") {
                try {
                    val userId = call.principal<Int>()!!
                    val user = authService.getUserFromToken(call.request.headers["Authorization"]!!.removePrefix("Bearer "))
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, mapOf(
                            "id" to user.id,
                            "username" to user.username,
                            "email" to user.email,
                            "createdAt" to user.createdAt.toString()
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                }
            }
        }
    }
}
