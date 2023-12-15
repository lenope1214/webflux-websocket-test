plugins {
    id("org.springframework.boot") version "2.7.0"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
    kotlin("plugin.allopen") version "1.9.21"
    kotlin("plugin.noarg") version "1.9.21"
}

group = "io.leeseongbok"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.7.0")  // webflux include web client
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
    annotation("com.fasterxml.jackson.databind.annotation.JsonDeserialize")
    annotation("software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
