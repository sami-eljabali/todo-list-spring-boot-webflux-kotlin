# Spring Boot WebFlux Kotlin Template

[![Build & Test](https://github.com/sami-eljabali/todo-list-spring-boot-webflux-kotlin/actions/workflows/build.yml/badge.svg)](https://github.com/sami-eljabali/todo-list-spring-boot-webflux-kotlin/actions/workflows/build.yml)
[![Lint](https://github.com/sami-eljabali/todo-list-spring-boot-webflux-kotlin/actions/workflows/lint.yml/badge.svg)](https://github.com/sami-eljabali/todo-list-spring-boot-webflux-kotlin/actions/workflows/lint.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-blue.svg)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A GitHub template for reactive REST APIs with Spring Boot 4, Kotlin coroutines, and R2DBC. The
build, the test suite, and the local database are already wired up, so you can clone it and start
writing code.

## Highlights

- Reactive from the HTTP layer down to the database, with WebFlux and R2DBC
- Coroutines throughout, including `CoroutineCrudRepository` for persistence
- Tests that run against a real PostgreSQL through Testcontainers, so there is no database to install
- A Docker Compose setup that starts PostgreSQL together with the application
- OpenAPI documentation and Swagger UI out of the box

## Stack

| Component | Choice |
|---|---|
| Language | Kotlin 2.4.10 |
| Runtime | Java 25 (Amazon Corretto), with virtual threads enabled |
| Framework | Spring Boot 4.2, WebFlux (reactive, non-blocking) |
| Database | PostgreSQL over R2DBC (`CoroutineCrudRepository`) |
| Security | Spring Security with HTTP Basic. Swap it for an OAuth2 Resource Server in production. |
| API docs | SpringDoc OpenAPI 3, Swagger UI at `:9090/swagger-ui/index.html` |
| Build | Gradle 9.9 with a version catalog (`gradle/libs.versions.toml`) |
| Linting | Kotlinter |
| Testing | JUnit 5, Kotest assertions, MockK, Testcontainers (PostgreSQL) |

> [!NOTE]
> Kotlin stays on 2.4.10 because ktlint 1.8.0, the version bundled with Kotlinter 5.7.0, fails on
> the Kotlin 2.4.20 and later compilers with *"Extensions storage is not registered"*. You can move
> to Kotlin 2.4.2x once a ktlint release that supports it reaches Maven Central.

---

## Prerequisites

Before you start, install:

- Java 25 ([Amazon Corretto](https://aws.amazon.com/corretto/))
- Docker, for the local PostgreSQL and for Testcontainers

### Use the template

1. Click **Use this template** on GitHub.
2. Replace the root package `org.eljabali.sami.todo` with your own across all source files.
3. Rename the project in [`settings.gradle.kts`](settings.gradle.kts).
4. Update `spring.r2dbc.url` and the credentials in
   [`src/main/resources/application.yml`](src/main/resources/application.yml).
5. Update the schema SQL in [`src/main/resources/schema.sql`](src/main/resources/schema.sql). Both
   the application and the Testcontainers tests apply it.

---

## Run the application

> [!TIP]
> No database installed? Skip to [With Testcontainers](#with-testcontainers).

### With Docker Compose

To start the application and its database together, run:

```bash
./gradlew bootRun
```

The `spring-boot-docker-compose` module sits on the development classpath, so the services in
`docker-compose.yml` start before the application and stop with it.

### With Testcontainers

The `TestApplication` in the test sources starts the application against a PostgreSQL container
that Testcontainers manages, so you do not need a local database. Run it from your IDE, or run:

```bash
./gradlew bootTestRun
```

### Run the packaged JAR

To run the application as a standalone process, start the database first and then run the
executable JAR:

1. Start the database:

   ```bash
   docker compose up -d
   ```

2. Build the executable JAR:

   ```bash
   ./gradlew build
   ```

3. Run the JAR:

   ```bash
   java -jar build/libs/todo-0.0.1-SNAPSHOT.jar
   ```

### Explore APIs with Swagger UI

The API is at `http://localhost:8080/v1/todos`, and Swagger UI is at
`http://localhost:9090/swagger-ui/index.html`. Authenticate with `user` / `password` or
`admin` / `password`; see `SecurityConfig`.

---

## Tests

The table below lists each test and the database it runs against.

| Test | Scope | Database |
|---|---|---|
| `AppPropertiesTest` | The `app.*` configuration properties | None |
| `TodoControllerTest` | The controller endpoints, `WebTestClient` against a `@WebFluxTest` slice | Mocked repository |
| `TodoRepositoryTest` | The R2DBC repository, with `@DataR2dbcTest` | PostgreSQL from Testcontainers |
| `IntegrationTest` | The whole application over real HTTP, with `WebClient` | PostgreSQL from Testcontainers |

`TestcontainersConfiguration` is a `@TestConfiguration` that declares a `PostgreSQLContainer` bean
annotated with `@ServiceConnection`. Spring Boot derives the `spring.r2dbc.*` settings from the
running container, so the tests need neither `@DynamicPropertySource` nor the `@Testcontainers` and
`@Container` annotations.

The tests need a running Docker daemon; Testcontainers starts a PostgreSQL container for them.
To run the unit, controller, repository, and integration tests:

```bash
./gradlew test
```

---

## Lint and format

Kotlinter checks the Kotlin sources against the ktlint style rules, and the build fails when a
rule is broken.

| Command | Purpose |
|---|---|
| `./gradlew check` | Run the tests and the lint check |
| `./gradlew lintKotlin` | Check the Kotlin sources with Kotlinter |
| `./gradlew formatKotlin` | Format the Kotlin sources with Kotlinter |

---

## Project layout

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

---

## Build

| Command | Purpose |
|---|---|
| `./gradlew build` | Compile, lint, and run all tests |
| `./gradlew bootJar` | Build the executable JAR in `build/libs/` |

---

## Docker

To build and run the production image:

1. Build the executable JAR: `./gradlew bootJar`
2. Build the image: `docker build -t todo-app .`
3. Run the image: `docker run -p 8080:8080 todo-app`

---

## CI

[`.github/workflows/build.yml`](.github/workflows/build.yml) compiles the project and runs the full
test suite. The tests rely on Testcontainers, and Docker is preinstalled on the GitHub-hosted
runners, so the workflow needs neither a database service nor a `docker compose up` step.

---

## License

Released under the [MIT License](LICENSE).
