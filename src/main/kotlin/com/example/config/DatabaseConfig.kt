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
            // Create tables if they don't exist
            SchemaUtils.createMissingTablesAndColumns(Users, Tasks)
        }
    }
}
