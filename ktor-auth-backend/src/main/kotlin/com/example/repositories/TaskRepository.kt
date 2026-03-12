package com.example.repositories

import com.example.config.Tasks
import com.example.models.Task
import com.example.models.TaskCreateRequest
import com.example.models.TaskUpdateRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface TaskRepository {
    fun createTask(userId: Int, request: TaskCreateRequest): Task
    fun getTaskById(id: Int): Task?
    fun getTasksByUserId(userId: Int): List<Task>
    fun getAllTasks(): List<Task>
    fun updateTask(id: Int, userId: Int, request: TaskUpdateRequest): Task?
    fun deleteTask(id: Int, userId: Int): Boolean
    fun deleteAllTasksByUser(userId: Int): Int
}

class TaskRepositoryImpl : TaskRepository {
    override fun createTask(userId: Int, request: TaskCreateRequest): Task {
        return transaction {
            val now = LocalDateTime.now()
            val insertStatement = Tasks.insert {
                it[Tasks.userId] = userId
                it[title] = request.title
                it[description] = request.description
                it[isCompleted] = request.isCompleted
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            toTask(insertStatement.resultedValues!!.first())
        }
    }
    
    override fun getTaskById(id: Int): Task? {
        return transaction {
            Tasks.select { Tasks.id eq id }
                .map { toTask(it) }
                .singleOrNull()
        }
    }
    
    override fun getTasksByUserId(userId: Int): List<Task> {
        return transaction {
            Tasks.select { Tasks.userId eq userId }
                .orderBy(Tasks.createdAt to SortOrder.DESC)
                .map { toTask(it) }
        }
    }
    
    override fun getAllTasks(): List<Task> {
        return transaction {
            Tasks.selectAll()
                .orderBy(Tasks.createdAt to SortOrder.DESC)
                .map { toTask(it) }
        }
    }
    
    override fun updateTask(id: Int, userId: Int, request: TaskUpdateRequest): Task? {
        return transaction {
            val task = Tasks.select { (Tasks.id eq id) and (Tasks.userId eq userId) }
                .singleOrNull()
            
            if (task != null) {
                val updateMap = mutableMapOf<Column<*>, Any>()
                request.title?.let { updateMap[Tasks.title] = it }
                request.description?.let { updateMap[Tasks.description] = it }
                request.isCompleted?.let { updateMap[Tasks.isCompleted] = it }
                updateMap[Tasks.updatedAt] = LocalDateTime.now()
                
                if (updateMap.isNotEmpty()) {
                    Tasks.update({ (Tasks.id eq id) and (Tasks.userId eq userId) }) {
                        updateMap.forEach { (column, value) ->
                            it[column] = value
                        }
                    }
                }
                
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
    
    override fun deleteAllTasksByUser(userId: Int): Int {
        return transaction {
            Tasks.deleteWhere { Tasks.userId eq userId }
        }
    }
    
    private fun toTask(row: ResultRow): Task {
        return Task(
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
