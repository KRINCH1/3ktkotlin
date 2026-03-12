package com.example.exceptions

data class ApiError(
    val statusCode: Int,
    val message: String,
    val path: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class AppException(
    message: String,
    val statusCode: Int
) : Exception(message)

class BadRequestException(message: String) : AppException(message, 400)
class UnauthorizedException(message: String) : AppException(message, 401)
class NotFoundException(message: String) : AppException(message, 404)
class ConflictException(message: String) : AppException(message, 409)
