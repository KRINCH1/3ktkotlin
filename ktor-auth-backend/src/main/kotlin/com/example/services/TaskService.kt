package com.example.services

import com.example.exceptions.BadRequestException
import com.example.exceptions.NotFoundException
import com.example.exceptions.UnauthorizedException
import com.example.models.*
import com.example.repositories.TaskRepository
import com.example.repositories.UserRepository

interface TaskService {
    fun createTask(userId: Int, request: TaskCreateRequest): Task
    fun getTaskById(userId: Int, taskId: Int): Task
    fun getUserTasks(userId: Int): List<Task>
    fun getAllTasks(): List<Task>
    fun updateTask(userId: Int, taskId: Int, request: TaskUpdateRequest): Task
    fun deleteTask(userId: Int, taskId: Int)
    fun validateTaskOwnership(userId: Int, taskId: Int): Task
}

class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : TaskService {
    
    override fun createTask(userId: Int, request: TaskCreateRequest): Task {
        // Validate user exists
        val user = userRepository.findById(userId)
            ?: throw UnauthorizedException("User not found")
        
        // Validate input
        if (request.title.isBlank()) {
            throw BadRequestException("Task title cannot be empty")
        }
        
        return taskRepository.createTask(userId, request)
    }
    
    override fun getTaskById(userId: Int, taskId: Int): Task {
        return validateTaskOwnership(userId, taskId)
    }
    
    override fun getUserTasks(userId: Int): List<Task> {
        // Validate user exists
        userRepository.findById(userId)
            ?: throw UnauthorizedException("User not found")
        
        return taskRepository.getTasksByUserId(userId)
    }
    
    override fun getAllTasks(): List<Task> {
        return taskRepository.getAllTasks()
    }
    
    override fun updateTask(userId: Int, taskId: Int, request: TaskUpdateRequest): Task {
        // Validate task exists and belongs to user
        validateTaskOwnership(userId, taskId)
        
        // Validate input
        if (request.title != null && request.title.isBlank()) {
            throw BadRequestException("Task title cannot be empty")
        }
        
        val updatedTask = taskRepository.updateTask(taskId, userId, request)
        return updatedTask ?: throw NotFoundException("Task not found")
    }
    
    override fun deleteTask(userId: Int, taskId: Int) {
        val deleted = taskRepository.deleteTask(taskId, userId)
        if (!deleted) {
            throw NotFoundException("Task not found")
        }
    }
    
    override fun validateTaskOwnership(userId: Int, taskId: Int): Task {
        val task = taskRepository.getTaskById(taskId)
            ?: throw NotFoundException("Task not found")
        
        if (task.userId != userId) {
            throw UnauthorizedException("You don't have permission to access this task")
        }
        
        return task
    }
}
