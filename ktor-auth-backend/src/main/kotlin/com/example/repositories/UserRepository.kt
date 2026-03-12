package com.example.repositories

import com.example.config.Users
import com.example.models.User
import com.example.models.UserCreateRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface UserRepository {
    fun createUser(request: UserCreateRequest, passwordHash: String): User
    fun findByEmail(email: String): User?
    fun findById(id: Int): User?
    fun findByUsername(username: String): User?
    fun updateUser(id: Int, username: String?, email: String?): User?
    fun deleteUser(id: Int): Boolean
}

class UserRepositoryImpl : UserRepository {
    override fun createUser(request: UserCreateRequest, passwordHash: String): User {
        return transaction {
            val now = LocalDateTime.now()
            val insertStatement = Users.insert {
                it[username] = request.username
                it[email] = request.email
                it[passwordHash] = passwordHash
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            User(
                id = insertStatement[Users.id],
                username = insertStatement[Users.username],
                email = insertStatement[Users.email],
                passwordHash = insertStatement[Users.passwordHash],
                createdAt = insertStatement[Users.createdAt],
                updatedAt = insertStatement[Users.updatedAt]
            )
        }
    }
    
    override fun findByEmail(email: String): User? {
        return transaction {
            Users.select { Users.email eq email }
                .map { toUser(it) }
                .singleOrNull()
        }
    }
    
    override fun findById(id: Int): User? {
        return transaction {
            Users.select { Users.id eq id }
                .map { toUser(it) }
                .singleOrNull()
        }
    }
    
    override fun findByUsername(username: String): User? {
        return transaction {
            Users.select { Users.username eq username }
                .map { toUser(it) }
                .singleOrNull()
        }
    }
    
    override fun updateUser(id: Int, username: String?, email: String?): User? {
        return transaction {
            val updateMap = mutableMapOf<Column<*>, Any>()
            username?.let { updateMap[Users.username] = it }
            email?.let { updateMap[Users.email] = it }
            updateMap[Users.updatedAt] = LocalDateTime.now()
            
            if (updateMap.isNotEmpty()) {
                Users.update({ Users.id eq id }) {
                    updateMap.forEach { (column, value) ->
                        it[column] = value
                    }
                }
            }
            
            findById(id)
        }
    }
    
    override fun deleteUser(id: Int): Boolean {
        return transaction {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }
    
    private fun toUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            createdAt = row[Users.createdAt],
            updatedAt = row[Users.updatedAt]
        )
    }
}
