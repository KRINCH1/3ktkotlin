package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    @Contextual val createdAt: LocalDateTime,
    @Contextual val updatedAt: LocalDateTime
)

@Serializable
data class UserCreateRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val username: String,
    val email: String
)
