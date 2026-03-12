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
import org.slf4j.LoggerFactory

fun Route.taskRoutes(taskService: TaskService) {
    val logger = LoggerFactory.getLogger("TaskRoutes")
    
    authenticate("auth-jwt") {
        route("/api/tasks") {
            // Get all tasks for current user
            get {
                try {
                    val userId = call.principal<Int>()!!
                    val tasks = taskService.getUserTasks(userId)
                    call.respond(HttpStatusCode.OK, tasks)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Get tasks error", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
                }
            }
            
            // Create new task
            post {
                try {
                    val userId = call.principal<Int>()!!
                    val request = call.receive<TaskCreateRequest>()
                    val task = taskService.createTask(userId, request)
                    call.respond(HttpStatusCode.Created, task)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Create task error", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
                }
            }
            
            // Get task by id
            get("/{id}") {
                try {
                    val userId = call.principal<Int>()!!
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid task ID")
                    
                    val task = taskService.getTaskById(userId, taskId)
                    call.respond(HttpStatusCode.OK, task)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Get task error", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
                }
            }
            
            // Update task
            put("/{id}") {
                try {
                    val userId = call.principal<Int>()!!
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid task ID")
                    
                    val request = call.receive<TaskUpdateRequest>()
                    val task = taskService.updateTask(userId, taskId, request)
                    call.respond(HttpStatusCode.OK, task)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Update task error", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
                }
            }
            
            // Delete task
            delete("/{id}") {
                try {
                    val userId = call.principal<Int>()!!
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid task ID")
                    
                    taskService.deleteTask(userId, taskId)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: AppException) {
                    call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Delete task error", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
                }
            }
        }
        
        // Admin route - get all tasks (for demonstration)
        get("/api/admin/tasks") {
            try {
                val userId = call.principal<Int>()!!
                // In a real app, check if user is admin
                val tasks = taskService.getAllTasks()
                call.respond(HttpStatusCode.OK, tasks)
            } catch (e: AppException) {
                call.respond(HttpStatusCode.fromValue(e.statusCode), mapOf("error" to e.message))
            } catch (e: Exception) {
                logger.error("Get all tasks error", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
            }
        }
    }
}
