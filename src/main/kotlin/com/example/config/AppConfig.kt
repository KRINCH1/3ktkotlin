package com.example.config

import io.github.cdimascio.dotenv.dotenv

object AppConfig {
    private val dotenv = dotenv {
        directory = "./"
        filename = ".env"
        ignoreIfMissing = true
    }
    
    fun getEnv(key: String, defaultValue: String? = null): String {
        return dotenv[key] ?: System.getenv(key) ?: defaultValue ?: 
            throw IllegalStateException("Environment variable  not found")
    }
    
    val databaseUrl: String by lazy { getEnv("DB_URL", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1") }
    val databaseDriver: String by lazy { getEnv("DB_DRIVER", "org.h2.Driver") }
    val databaseUser: String by lazy { getEnv("DB_USER", "sa") }
    val databasePassword: String by lazy { getEnv("DB_PASSWORD", "") }
    
    val jwtSecret: String by lazy { getEnv("JWT_SECRET", "default-secret-key-change-me") }
    val jwtIssuer: String by lazy { getEnv("JWT_ISSUER", "http://localhost:8080") }
    val jwtAudience: String by lazy { getEnv("JWT_AUDIENCE", "http://localhost:8080") }
    val jwtExpiration: Long by lazy { getEnv("JWT_EXPIRATION", "86400000").toLong() }
}
