package com.example.config

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    
    override val primaryKey = PrimaryKey(id)
}

object Tasks : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val isCompleted = bool("is_completed").default(false)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    
    override val primaryKey = PrimaryKey(id)
}

object DatabaseConfig {
    fun init() {
        Database.connect(
            url = AppConfig.databaseUrl,
            driver = AppConfig.databaseDriver,
            user = AppConfig.databaseUser,
            password = AppConfig.databasePassword
        )
        
        transaction {
            SchemaUtils.create(Users, Tasks)
            
            // Seed data for testing
            if (Users.selectAll().empty()) {
                val hashedPassword = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults()
                    .hashToString(12, "password123".toCharArray())
                
                val userId = Users.insert {
                    it[username] = "testuser"
                    it[email] = "test@example.com"
                    it[passwordHash] = hashedPassword
                    it[createdAt] = LocalDateTime.now()
                    it[updatedAt] = LocalDateTime.now()
                } get Users.id
                
                Tasks.insert {
                    it[Tasks.userId] = userId
                    it[title] = "Sample Task 1"
                    it[description] = "This is a sample task"
                    it[createdAt] = LocalDateTime.now()
                    it[updatedAt] = LocalDateTime.now()
                }
                
                Tasks.insert {
                    it[Tasks.userId] = userId
                    it[title] = "Sample Task 2"
                    it[description] = "Another sample task"
                    it[createdAt] = LocalDateTime.now()
                    it[updatedAt] = LocalDateTime.now()
                }
            }
        }
    }
}
