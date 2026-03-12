import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

group = "com.example"
version = "1.0.0"

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor core
    implementation("io.ktor:ktor-server-core-jvm:2.3.5")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.5")
    
    // Authentication
    implementation("io.ktor:ktor-server-auth-jvm:2.3.5")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.5")
    
    // Database
    implementation("com.h2database:h2:2.2.224")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    
    // Security
    implementation("at.favre.lib:bcrypt:0.10.2")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
    
    // Configuration
    implementation("io.ktor:ktor-server-config-yaml:2.3.5")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    
    // CORS
    implementation("io.ktor:ktor-server-cors-jvm:2.3.5")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.5")
    
    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")
    testImplementation("io.ktor:ktor-client-content-negotiation-jvm:2.3.5")
    testImplementation("com.h2database:h2:2.2.224")
}

kotlin {
    jvmToolchain(17)
}
