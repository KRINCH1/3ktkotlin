package com.example.repositories

import com.example.config.Tasks
import com.example.models.Task
import com.example.models.TaskCreateRequest
import com.example.models.TaskUpdateRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

interface TaskRepository {
    fun createTask(userId: Int, request: TaskCreateRequest): Task
    fun getTaskById(id: Int): Task?
    fun getTasksByUserId(userId: Int): List<Task>
    fun updateTask(id: Int, userId: Int, request: TaskUpdateRequest): Task?
    fun deleteTask(id: Int, userId: Int): Boolean
}

class TaskRepositoryImpl : TaskRepository {
    override fun createTask(userId: Int, request: TaskCreateRequest): Task {
        return transaction {
            val now = LocalDateTime.now()
            val insertStatement = Tasks.insert { row ->
                row[Tasks.userId] = userId
                row[Tasks.title] = request.title
                row[Tasks.description] = request.description
                row[Tasks.isCompleted] = request.isCompleted
                row[Tasks.createdAt] = now
                row[Tasks.updatedAt] = now
            }
            
            val id = insertStatement[Tasks.id]
            Task(
                id = id,
                userId = userId,
                title = request.title,
                description = request.description,
                isCompleted = request.isCompleted,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    override fun getTaskById(id: Int): Task? {
        return transaction {
            Tasks.select { Tasks.id eq id }
                .map { row ->
                    Task(
                        id = row[Tasks.id],
                        userId = row[Tasks.userId],
                        title = row[Tasks.title],
                        description = row[Tasks.description],
                        isCompleted = row[Tasks.isCompleted],
                        createdAt = row[Tasks.createdAt],
                        updatedAt = row[Tasks.updatedAt]
                    )
                }
                .singleOrNull()
        }
    }
    
    override fun getTasksByUserId(userId: Int): List<Task> {
        return transaction {
            Tasks.select { Tasks.userId eq userId }
                .orderBy(Tasks.createdAt to SortOrder.DESC)
                .map { row ->
                    Task(
                        id = row[Tasks.id],
                        userId = row[Tasks.userId],
                        title = row[Tasks.title],
                        description = row[Tasks.description],
                        isCompleted = row[Tasks.isCompleted],
                        createdAt = row[Tasks.createdAt],
                        updatedAt = row[Tasks.updatedAt]
                    )
                }
        }
    }
    
    override fun updateTask(id: Int, userId: Int, request: TaskUpdateRequest): Task? {
        return transaction {
            val updateCount = Tasks.update({ (Tasks.id eq id) and (Tasks.userId eq userId) }) { row ->
                request.title?.let { row[Tasks.title] = it }
                request.description?.let { row[Tasks.description] = it }
                request.isCompleted?.let { row[Tasks.isCompleted] = it }
                row[Tasks.updatedAt] = LocalDateTime.now()
            }
            
            if (updateCount > 0) {
                getTaskById(id)
            } else {
                null
            }
        }
    }
    
    override fun deleteTask(id: Int, userId: Int): Boolean {
        return transaction {
            Tasks.deleteWhere { (Tasks.id eq id) and (Tasks.userId eq userId) } > 0
        }
    }
}
