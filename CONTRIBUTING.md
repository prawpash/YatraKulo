# Contributing to YatraKulo

First off, thank you for considering contributing to YatraKulo! It's people like you that make YatraKulo such a great platform.

## 1. Local Development Setup

We use a `Makefile` to streamline local development. You'll need Docker, Java 21, and Node.js installed.

1. Fork the repo and clone it locally.
2. Spin up the infrastructure: `cd infra && docker-compose up -d`
3. Start the services you are working on:
   ```bash
   make dev-auth
   make dev-ledger
   # see 'make help' for more options
   ```

## 2. Code Style & Standards

### Java Services
- We use Java 21.
- Please follow standard Spring Boot conventions.
- All new features MUST include JUnit 5 tests.
- Database migrations must be placed in `infra/db/{service}/migrations` using the Flyway naming convention (`V{version}__{description}.sql`).

### Node.js / NestJS Services
- We use TypeScript and `pnpm`.
- Ensure your code passes standard ESLint/Prettier checks before committing.
- Place all business logic in services, keeping controllers thin.

## 3. Pull Request Process
1. Do not commit build outputs, caches, or generated artifacts.
2. Update the README.md with details of changes to the interface, this includes new environment variables, exposed ports, useful file locations and container parameters.
3. You may merge the Pull Request in once you have the sign-off of two other developers, or if you do not have permission to do that, you may request the second reviewer to merge it for you.
