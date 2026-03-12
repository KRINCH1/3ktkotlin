package com.example.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AppConfig
import com.example.exceptions.BadRequestException
import com.example.exceptions.ConflictException
import com.example.exceptions.UnauthorizedException
import com.example.models.*
import com.example.repositories.UserRepository
import java.util.*

interface AuthService {
    fun register(request: UserCreateRequest): AuthResponse
    fun login(request: UserLoginRequest): AuthResponse
    fun validateToken(token: String): Int
    fun getUserFromToken(token: String): User?
}

class AuthServiceImpl(
    private val userRepository: UserRepository
) : AuthService {
    
    private val algorithm = Algorithm.HMAC256(AppConfig.jwtSecret)
    private val validator = JWT.require(algorithm)
        .withIssuer(AppConfig.jwtIssuer)
        .withAudience(AppConfig.jwtAudience)
        .build()
    
    override fun register(request: UserCreateRequest): AuthResponse {
        // Validate input
        if (request.username.length < 3) {
            throw BadRequestException("Username must be at least 3 characters long")
        }
        if (request.password.length < 6) {
            throw BadRequestException("Password must be at least 6 characters long")
        }
        if (!request.email.contains("@")) {
            throw BadRequestException("Invalid email format")
        }
        
        // Check if user exists
        val existingUser = userRepository.findByEmail(request.email)
        if (existingUser != null) {
            throw ConflictException("User with this email already exists")
        }
        
        val existingUsername = userRepository.findByUsername(request.username)
        if (existingUsername != null) {
            throw ConflictException("Username already taken")
        }
        
        // Hash password
        val passwordHash = BCrypt.withDefaults()
            .hashToString(12, request.password.toCharArray())
        
        // Create user
        val user = userRepository.createUser(request, passwordHash)
        
        // Generate token
        val token = generateToken(user.id)
        
        return AuthResponse(
            token = token,
            userId = user.id,
            username = user.username,
            email = user.email
        )
    }
    
    override fun login(request: UserLoginRequest): AuthResponse {
        // Find user
        val user = userRepository.findByEmail(request.email)
            ?: throw UnauthorizedException("Invalid email or password")
        
        // Verify password
        val result = BCrypt.verifyer().verify(
            request.password.toCharArray(),
            user.passwordHash
        )
        
        if (!result.verified) {
            throw UnauthorizedException("Invalid email or password")
        }
        
        // Generate token
        val token = generateToken(user.id)
        
        return AuthResponse(
            token = token,
            userId = user.id,
            username = user.username,
            email = user.email
        )
    }
    
    override fun validateToken(token: String): Int {
        try {
            val decoded = validator.verify(token)
            return decoded.getClaim("userId").asInt()
        } catch (e: Exception) {
            throw UnauthorizedException("Invalid or expired token")
        }
    }
    
    override fun getUserFromToken(token: String): User? {
        try {
            val userId = validateToken(token)
            return userRepository.findById(userId)
        } catch (e: Exception) {
            return null
        }
    }
    
    private fun generateToken(userId: Int): String {
        return JWT.create()
            .withIssuer(AppConfig.jwtIssuer)
            .withAudience(AppConfig.jwtAudience)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + AppConfig.jwtExpiration))
            .sign(algorithm)
    }
}
