package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Task(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class TaskCreateRequest(
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false
)

@Serializable
data class TaskUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val isCompleted: Boolean? = null
)

@Serializable
data class TaskResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
