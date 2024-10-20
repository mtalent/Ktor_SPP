import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version = "2.0.21"
val logback_version = "1.4.12"
val ktor_version = "3.0.0"

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.spp.Talent"
//version = "0.0.1"

application {
    mainClass.set("com.spp.Talent.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {

    // Exposed core dependencies
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1") // for datetime support

    // MySQL database connector
    //implementation("mysql:mysql-connector-java:8.2.0")

    // HikariCP for connection pooling
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Coroutine support (if needed for async tasks)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")


    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("com.mysql:mysql-connector-j:8.0.33")
    implementation("io.ktor:ktor-server-netty:$kotlin_version") // Make sure the version is correct
    implementation("io.ktor:ktor-network-tls:$kotlin_version")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:2.3.12")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    shadowJar {
        archiveFileName.set("Ktor_SPP.jar") // Set the name of the jar file
    }
}
