# YatraKulo Architecture

This document provides a deep dive into the technical architecture of the YatraKulo platform. It is intended for developers, contributors, and technical reviewers who want to understand the design decisions, patterns, and technologies powering this system.

## 1. System Overview

YatraKulo utilizes a **Polyglot Microservices Architecture** to maximize the strengths of different ecosystems while ensuring high scalability, modularity, and team agility.

### Why Polyglot?
- **Java & Spring Boot:** Used for highly complex, transaction-heavy, and strictly typed domains like Authentication and Ledger management. Spring Boot provides robust security (OAuth2), transaction management, and mature testing frameworks.
- **Node.js & NestJS:** Used for I/O bound, highly concurrent services like Account and Transaction management, benefiting from the non-blocking nature of V8.
- **Edge Computing (Cloudflare Workers):** Used for the Notification service to guarantee ultra-low latency delivery of alerts and webhooks globally, without putting load on the core infrastructure.

## 2. Component Details

### Kong API Gateway
All external traffic is routed through a **Kong API Gateway**.
- **Role:** Handles routing, rate limiting, request validation, and acts as the first line of defense.
- **Custom Logic:** We utilize custom Lua plugins (e.g., `redis-permission-resolver`) to evaluate permissions securely and efficiently before traffic ever hits our backend microservices.
- **Configuration:** Managed declaratively via `kong.yml`.

### The Microservices
1. **Authentication Service (Java/Spring Boot)**
   - Manages OAuth2 flows, user identities, and secure token issuance.
   - Stores session state and distributed locks in **Redis**.
2. **Ledger Service (Java/Spring Boot)**
   - The financial core. Handles double-entry accounting for fiat transactions, ensuring ACID compliance utilizing PostgreSQL.
3. **Account Service (Node.js/NestJS)**
   - Manages user profiles, settings, and preferences.
4. **Transaction Service (Node.js/NestJS)**
   - Handles the high-throughput ingestion of raw transactional data before routing it to the strict Ledger service.
5. **Notification Service (Cloudflare Worker)**
   - Deployed to the edge to manage asynchronous alerts (email, push, in-app) with ultra-low latency.

## 3. Data Strategy

We adhere to the **Database-per-Service** pattern to ensure loose coupling.
- **RDBMS:** Each service has its own isolated logical database within a PostgreSQL cluster (e.g., `yatrakulo_authentication`, `yatrakulo_ledger`).
- **Migrations:** Database schemas are strictly version-controlled using **Flyway**. Migrations are executed securely as part of the Gradle build lifecycle (e.g., `./gradlew :services:authentication:flywayMigrate`).
- **Caching:** Redis is used for fast, ephemeral data storage, rate-limiting counters, and session management.

## 4. Testing & Quality Assurance

Quality is non-negotiable in financial software.
- **Integration Testing:** We utilize **JUnit 5** alongside **Testcontainers**. During tests, ephemeral PostgreSQL and Redis Docker containers are spun up to ensure tests run against real database engines rather than in-memory mocks (like H2), preventing environment-specific bugs.
- **Code Coverage:** Enforced via Jacoco during the Gradle build pipeline.

## 5. Observability

To maintain a healthy, highly-available distributed system, we have integrated a comprehensive enterprise observability stack. **Note:** The observability infrastructure runs on external, dedicated servers to ensure logging and metrics processing do not consume core application resources.

- **Log Management (ELK):** Centralized logging using **Filebeat** to ship logs to an external **ELK Stack** (Elasticsearch, Logstash, Kibana), aggregating logs across all microservices and the API gateway.
- **Metrics & Monitoring:** Service metrics are exposed via Spring Boot Actuator and Micrometer, which are actively scraped by our external **Prometheus** server for real-time alerting and visualization.
- **Distributed Tracing:** Complete end-to-end request tracing utilizing **OpenTelemetry** and **Jaeger**. This allows us to track performance bottlenecks as requests traverse from the Kong Gateway down through the Java microservices, Node.js services, and Cloudflare Workers.
