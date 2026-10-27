# diet-dash-backend

REST API for a web app that nutritionists use to manage patient profiles, daily meal plans and a product database, with Word/Excel export.

## Context

Part of the Diet Dash startup (2024-2025), now closed. This repository is the original Spring Boot backend, archived as a portfolio piece (tag: `pre-ai`).
The current, final version is a separate monorepo (Hono + React): https://github.com/Sentimenta1ist/diet-dash

## Features

- Registration with e-mail confirmation, login, forgot/reset password flow; stateless JWT auth
- Patient profiles (body measurements, activity level, daily calorie and macro limits), CRUD plus partial update
- Day-by-day rations made of meals; add/update/delete meals, duplicate a day to other dates
- Product database (about 9.8k rows seeded from a bundled CSV, names in Ukrainian and English) plus user-created products
- Export of rations to `.docx` (Ukrainian/English headers) and `.xlsx`; a public read endpoint for sharing a ration
- Contact form, free/premium plan lookup per user, Swagger UI

## API overview

All endpoints are under `/api`. Everything except `/api/auth/**`, Swagger and `/api/ration/public` requires a `Bearer` JWT.

| Area | Endpoints |
|------|-----------|
| Auth | `POST /auth/register`, `/auth/login`, `/auth/confirm?token=`, `/auth/send-forgot-email`, `/auth/update-password?token=`, `/auth/submit` (contact form), `/auth/checkNew` |
| Profiles | `GET /profile`, `GET /profile/list`, `GET/PUT/PATCH/DELETE /profile/{id}`, `POST /profile`, `GET /profile/activities` |
| Rations | `GET /ration/{profileId}?date=`, `POST /ration/add/{profileId}`, `PUT /ration/update/{mealId}`, `DELETE /ration/delete/{mealId}`, `POST /ration/duplicate/{profileId}` |
| Export | `GET /ration/export` (docx), `GET /ration/exportXlsx`, `GET /ration/public` |
| Products | `GET /products`, `/products/base`, `/products/created`, `POST /products`, `POST /products/fill` (seed from CSV) |
| Misc | `GET /users/plan`, `GET /charts/main` |

## Architecture

```
React SPA --HTTPS--> nginx --/api/--> Spring Boot (this repo) --JDBC--> PostgreSQL
                                          |                              (schema by Liquibase)
                                          +--HTTPS--> Mailtrap send API (confirmation / reset e-mails)
```

nginx config and the Docker setup are in the repo; the SPA itself is not.

## Stack

| Layer | Technology |
|-------|-----------|
| Language / framework | Java 21, Spring Boot 3.1.4 (Web, Security, Data JPA, Validation) |
| Auth | `java-jwt` (HMAC256, 1-week tokens), BCrypt password hashing |
| DB | PostgreSQL, Liquibase (YAML changelogs), Hibernate `ddl-auto: validate` |
| Mapping / docs | MapStruct, Lombok, springdoc-openapi |
| Export / e-mail | Apache POI (docx, xlsx), OkHttp to the Mailtrap API |
| Delivery | Dockerfile (multi-stage Maven build), GitHub Actions image build and SSH deploy |

## Run locally

Requirements: JDK 21, Maven (or `./mvnw`), PostgreSQL (`docker compose up -d postgres` starts one on port 5432).

1. Start PostgreSQL. The `idea` profile (`application-idea.yaml`) targets `localhost:5432` and matches `docker-compose.yml`; the `local` profile targets a Docker-network host named `postgres`.
2. Export the required environment variables (names only; provide your own values):
   - `POSTGRES_PASSWORD` (used by `application-local.yaml`; the `idea` profile has its own dev credentials)
   - `MAIL_PASSWORD` (`spring.mail.password`)
   - `MAILTRAP_API_TOKEN` (`mailtrap.api.token`)
   - optional: `SECURITY_JWT_TOKEN_SECRET_KEY` (`security.jwt.token.secret-key`; a weak default is used otherwise)
3. `spring.liquibase.default-schema` is referenced but not defined in the YAML files; pass it as a property or env var (`SPRING_LIQUIBASE_DEFAULT_SCHEMA`).
4. Run: `./mvnw spring-boot:run -Dspring-boot.run.profiles=idea`, then open `/api/swagger-ui.html`.
5. Seed products once: `POST /api/products/fill` (authenticated).

Docker: `./mvnw clean package`, `docker build -t diet-dash-image .`, run with the same variables.

## Interesting bits

- Stateless JWT filter placed before `BasicAuthenticationFilter`; tokens carry only the login, and an unconfirmed account is rejected at token validation.
- `ExportService` builds the Word table by hand with Apache POI (merged day rows, per-column widths, meals grouped by time), localized by a `lang` parameter.
- Hand-rolled per-IP `RateLimitFilter` on registration, password reset and contact endpoints.
- Product search is language-aware (Ukrainian or English name columns) and paginated; bulk seeding reads the CSV and saves products in batches of 1000.

---
Written before AI coding assistants. Archived as-is.
