package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class Task(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    @Contextual val createdAt: LocalDateTime,
    @Contextual val updatedAt: LocalDateTime
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
