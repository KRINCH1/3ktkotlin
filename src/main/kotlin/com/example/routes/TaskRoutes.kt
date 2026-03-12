package com.example.routes

import com.example.exceptions.AppException
import com.example.models.TaskCreateRequest
import com.example.models.TaskUpdateRequest
import com.example.services.TaskService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(taskService: TaskService) {
    authenticate("auth-jwt") {
        route("/api/tasks") {
            get {
                try {
                    val principal = call.principal<UserIdPrincipal>()
                    val userId = principal?.name?.toInt() ?: throw IllegalArgumentException("User not authenticated")
                    val tasks = taskService.getUserTasks(userId)
                    call.respond(HttpStatusCode.OK, tasks)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                }
            }
            
            post {
                try {
                    val principal = call.principal<UserIdPrincipal>()
                    val userId = principal?.name?.toInt() ?: throw IllegalArgumentException("User not authenticated")
                    val request = call.receive<TaskCreateRequest>()
                    val task = taskService.createTask(userId, request)
                    call.respond(HttpStatusCode.Created, task)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                }
            }
            
            get("/{id}") {
                try {
                    val principal = call.principal<UserIdPrincipal>()
                    val userId = principal?.name?.toInt() ?: throw IllegalArgumentException("User not authenticated")
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid task ID")
                    
                    val task = taskService.getTaskById(userId, taskId)
                    call.respond(HttpStatusCode.OK, task)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                }
            }
            
            put("/{id}") {
                try {
                    val principal = call.principal<UserIdPrincipal>()
                    val userId = principal?.name?.toInt() ?: throw IllegalArgumentException("User not authenticated")
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid task ID")
                    
                    val request = call.receive<TaskUpdateRequest>()
                    val task = taskService.updateTask(userId, taskId, request)
                    call.respond(HttpStatusCode.OK, task)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                }
            }
            
            delete("/{id}") {
                try {
                    val principal = call.principal<UserIdPrincipal>()
                    val userId = principal?.name?.toInt() ?: throw IllegalArgumentException("User not authenticated")
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid task ID")
                    
                    taskService.deleteTask(userId, taskId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                }
            }
        }
    }
}
