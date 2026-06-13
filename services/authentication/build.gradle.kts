plugins {
    java
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ekapasha"
version = "0.0.1-SNAPSHOT"
description = "Authentication service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation(project(":libs:java-shared"))

    // Source: https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-scalar
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.0.1")

    // Source: https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Source: https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security-oauth2-resource-server
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")

    // Source: https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security-oauth2-authorization-server
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-authorization-server")

    // Source: https://mvnrepository.com/artifact/org.springframework/spring-jcl
    implementation("org.springframework:spring-jcl:6.2.15")

    // Source: https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-opentelemetry
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
