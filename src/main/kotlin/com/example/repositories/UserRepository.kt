package com.example.repositories

import com.example.config.Users
import com.example.models.User
import com.example.models.UserCreateRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

interface UserRepository {
    fun createUser(request: UserCreateRequest, passwordHash: String): User
    fun findByEmail(email: String): User?
    fun findById(id: Int): User?
    fun findByUsername(username: String): User?
}

class UserRepositoryImpl : UserRepository {
    override fun createUser(request: UserCreateRequest, passwordHash: String): User {
        return transaction {
            val now = LocalDateTime.now()
            val insertStatement = Users.insert { row ->
                row[Users.username] = request.username
                row[Users.email] = request.email
                row[Users.passwordHash] = passwordHash
                row[Users.createdAt] = now
                row[Users.updatedAt] = now
            }
            
            val id = insertStatement[Users.id]
            User(
                id = id,
                username = request.username,
                email = request.email,
                passwordHash = passwordHash,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    override fun findByEmail(email: String): User? {
        return transaction {
            Users.select { Users.email eq email }
                .map { row ->
                    User(
                        id = row[Users.id],
                        username = row[Users.username],
                        email = row[Users.email],
                        passwordHash = row[Users.passwordHash],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt]
                    )
                }
                .singleOrNull()
        }
    }
    
    override fun findById(id: Int): User? {
        return transaction {
            Users.select { Users.id eq id }
                .map { row ->
                    User(
                        id = row[Users.id],
                        username = row[Users.username],
                        email = row[Users.email],
                        passwordHash = row[Users.passwordHash],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt]
                    )
                }
                .singleOrNull()
        }
    }
    
    override fun findByUsername(username: String): User? {
        return transaction {
            Users.select { Users.username eq username }
                .map { row ->
                    User(
                        id = row[Users.id],
                        username = row[Users.username],
                        email = row[Users.email],
                        passwordHash = row[Users.passwordHash],
                        createdAt = row[Users.createdAt],
                        updatedAt = row[Users.updatedAt]
                    )
                }
                .singleOrNull()
        }
    }
}
