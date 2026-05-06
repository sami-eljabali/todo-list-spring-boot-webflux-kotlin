# Spring Boot WebFlux Kotlin Template

A GitHub template for reactive REST APIs using Spring Boot 4, Kotlin coroutines, and R2DBC.

## Stack

| | |
|---|---|
| **Language** | Kotlin 2.3 |
| **Runtime** | Java 25 (Amazon Corretto), virtual threads enabled |
| **Framework** | Spring Boot 4 — WebFlux (reactive, non-blocking) |
| **Database** | PostgreSQL via R2DBC (coroutine-first: `CoroutineCrudRepository`) |
| **Security** | Spring Security — HTTP Basic (swap for OAuth2 Resource Server in production) |
| **API Docs** | SpringDoc OpenAPI 3 — Swagger UI at `:9090/actuator/webjars/swagger-ui/index.html` |
| **Build** | Gradle 9 with version catalog (`gradle/libs.versions.toml`) |
| **Linting** | Kotlinter |

## Using this template

Click **Use this template** on GitHub, then:

1. Replace the root package `org.eljabali.sami.todo` with your own across all source files.
2. Rename the project in [`settings.gradle.kts`](settings.gradle.kts).
3. Update `spring.r2dbc.url` / credentials in [`application.properties`](src/main/resources/application.properties).
4. Add your schema SQL to [`pg-initdb.d/`](pg-initdb.d/) and [`src/test/resources/init.sql`](src/test/resources/init.sql).

## Prerequisites

- Java 25 ([Amazon Corretto](https://aws.amazon.com/corretto/))
- Docker (for local Postgres and Testcontainers)

## Local development

Start Postgres:

```bash
docker compose up -d
```

Run the app:

```bash
./gradlew bootRun
```

The API is available at `http://localhost:8080/v1/todos`.  
Swagger UI is at `http://localhost:9090/actuator/webjars/swagger-ui/index.html`.

## Tests

| Command | What it runs |
|---|---|
| `./gradlew test` | Unit + controller tests (no Docker required) |
| `./gradlew controllerTest` | `@WebFluxTest` slice tests only |
| `./gradlew repositoryTest` | `@DataR2dbcTest` slice tests (Testcontainers — needs Docker) |
| `./gradlew integrationTest` | Full `@SpringBootTest` tests (needs Docker Compose running) |
| `./gradlew check` | All of the above |

## Package structure

```
org.eljabali.sami.todo
├── Application.kt                  entry point
├── domain/
│   ├── model/                      entities and enums
│   ├── repository/                 CoroutineCrudRepository interfaces
│   └── exception/                  domain exceptions
├── application/                    Spring config classes
├── interfaces/
│   ├── Uris.kt                     centralized URI constants
│   ├── TodoController.kt           REST controllers
│   └── RestWebExceptionHandler.kt  @RestControllerAdvice
└── shared/
    └── model/                      request/response DTOs
```

## Build

```bash
./gradlew build          # compile + all tests
./gradlew bootJar        # fat JAR → build/libs/
./gradlew lintKotlin     # Kotlinter lint check
./gradlew formatKotlin   # Kotlinter auto-format
```

## Docker

Build and run the production image:

```bash
./gradlew bootJar
docker build -t todo-app .
docker run -p 8080:8080 todo-app
```
