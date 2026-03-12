package com.example.routes

import com.example.exceptions.AppException
import com.example.models.UserCreateRequest
import com.example.models.UserLoginRequest
import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/api/auth") {
        post("/register") {
            try {
                val request = call.receive<UserCreateRequest>()
                val response = authService.register(request)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: AppException) {
                call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
            }
        }
        
        post("/login") {
            try {
                val request = call.receive<UserLoginRequest>()
                val response = authService.login(request)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: AppException) {
                call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
            }
        }
    }
}
