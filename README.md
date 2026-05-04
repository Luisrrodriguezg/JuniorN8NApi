# Junior N8N API

Read-only product catalog API for **Junior de Barranquilla** merchandise, designed to be consumed by an AI chatbot built in n8n. The API exposes endpoints to query products, check availability, and get pricing — no purchase or write operations.

This is a **mockup project**: data resets on every restart, no authentication, in-memory database.

---

## Stack

- **Java 21** + **Spring Boot 3.3**
- **Spring Web** — REST endpoints
- **Spring Data JPA** — data access
- **H2** — in-memory database
- **Lombok** — boilerplate reduction
- **Docker** — containerized API + n8n

---

## Architecture

Layered monolith, organized by feature.

```
com.carinosas.juniorn8napi
└── product
    ├── controller        ← REST endpoints
    ├── service           ← business logic (low-stock rule, filtering)
    ├── repository        ← Spring Data JPA
    ├── domain            ← entity + enums
    ├── dto               ← API response shapes
    ├── mapper            ← entity → DTO
    └── exception         ← custom exceptions + global handler
```

**Data flow:** `Controller → Service → Repository → DB`, with `Mapper` converting entities to DTOs before responses leave the service.

---

## Domain model

Single `Product` entity:

| Field    | Type           | Notes                                      |
|----------|----------------|--------------------------------------------|
| `id`     | `Long`         | Auto-generated                             |
| `name`   | `String`       | e.g. "Camiseta Local 2025"                 |
| `type`   | `ProductType`  | enum (see below)                           |
| `size`   | `Size`         | enum (see below)                           |
| `price`  | `BigDecimal`   | COP, stored with 2 decimal precision       |
| `stock`  | `Integer`      | Drives `available` and `lowStock` flags    |

**Enums:**

- `Size`: `S`, `M`, `L`, `XL`, `None`
- `ProductType`: `CAMISETA_LOCAL`, `CAMISETA_VISITANTE`, `CAMISETA_ALTERNATIVA`, `PANTALONETA`, `MEDIAS`, `GORRA`, `PELUCHE_TIBURON`, `CAMISETA_PERRO`

**Derived (computed in mapper, not stored):**

- `available` = `stock > 0`
- `lowStock` = `available && stock < 20` (threshold configurable in `application.yml`)

---

## Endpoints

Base path: `/api/products`

| Method | Path                                            | Description                                         |
|--------|-------------------------------------------------|-----------------------------------------------------|
| GET    | `/api/products`                                 | Full catalog, sorted A→Z                            |
| GET    | `/api/products/available`                       | Only in-stock products, sorted A→Z, with low-stock flag |
| GET    | `/api/products/{id}`                            | Single product detail                               |
| GET    | `/api/products/search?name=&type=&size=`        | Filtered search (any combination of params)         |

### Example response

```json
{
  "id": 1,
  "name": "Camiseta Local 2025",
  "type": "CAMISETA_LOCAL",
  "size": "M",
  "price": 220000.00,
  "currency": "COP",
  "stock": 18,
  "available": true,
  "lowStock": true
}
```

### Error response

```json
{
  "error": "PRODUCT_NOT_FOUND",
  "message": "Product not found: id=999"
}
```

---

## Running locally (without Docker)

**Prerequisites:** Java 21, Maven 3.9+

```bash
mvn spring-boot:run
```

Then open:

- API: http://localhost:8080/api/products
- H2 console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:juniorstore`
  - User: `sa`
  - Password: *(empty)*

---

## Running with Docker (API + n8n)

**Prerequisites:** Docker Desktop running.

### 1. Create `.env` in the project root

```env
N8N_USER=admin
N8N_PASSWORD=changeme123
```

### 2. Build and start

```bash
docker compose up --build
```

First run takes ~2-3 minutes (downloads dependencies and the n8n image). Subsequent runs are ~10 seconds.

### 3. Open the services

| Service | URL                              | Credentials                    |
|---------|----------------------------------|--------------------------------|
| API     | http://localhost:8080/api/products | none                           |
| n8n     | http://localhost:5678            | `admin` / `changeme123`        |

### Useful Docker commands

```bash
docker compose up -d            # start in background
docker compose logs -f api      # follow API logs
docker compose logs -f n8n      # follow n8n logs
docker compose stop             # stop containers (preserves data)
docker compose down             # stop + remove containers (preserves volumes)
docker compose down -v          # full reset (wipes n8n workflows)
docker compose up --build       # rebuild after Java code changes
```

---

## Connecting n8n to the API

Inside the n8n container, your API is reachable at:

```
http://api:8080/api/products
```

**Not** `http://localhost:8080` — from inside the n8n container, `localhost` refers to n8n itself. The hostname `api` matches the service name in `docker-compose.yml` and resolves through Docker's internal network.

This is the most common gotcha. If your HTTP Request node returns "connection refused," check this first.

---

## Configuration

`src/main/resources/application.yml` exposes:

```yaml
store:
  low-stock-threshold: 20    # below this, lowStock = true
  currency: COP
```

Profiles:

- **default** — H2, verbose SQL logging, H2 console enabled (local dev)
- **docker** — H2, quiet logs, console disabled (containerized)

Activated via `SPRING_PROFILES_ACTIVE` environment variable.

---

## Seed data

Defined in `src/main/resources/data.sql`. Loaded on every startup (DB resets on restart since `ddl-auto=create-drop`).

Includes a mix of in-stock, low-stock, and out-of-stock products across all product types and sizes — designed to give the chatbot interesting things to say during demos.

---

## Project structure

```
junior-n8n-api/
├── src/main/
│   ├── java/com/carinosas/juniorn8napi/
│   │   ├── JuniorN8nApiApplication.java
│   │   └── product/
│   │       ├── controller/ProductController.java
│   │       ├── service/ProductService.java
│   │       ├── repository/ProductRepository.java
│   │       ├── domain/
│   │       │   ├── Product.java
│   │       │   ├── Size.java
│   │       │   └── ProductType.java
│   │       ├── dto/ProductResponse.java
│   │       ├── mapper/ProductMapper.java
│   │       └── exception/
│   │           ├── ProductNotFoundException.java
│   │           └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yml
│       ├── application-docker.yml
│       └── data.sql
├── pom.xml
├── Dockerfile
├── .dockerignore
├── docker-compose.yml
├── .env                    (gitignored)
└── README.md
```

---

## Future improvements (out of scope for the mockup)

- Swap H2 for PostgreSQL (separate container in compose)
- Add purchase/reservation endpoints with proper concurrency control
- Add authentication for write endpoints
- Migration management with Flyway/Liquibase
- Integration tests with Testcontainers
- OpenAPI/Swagger UI for n8n auto-discovery
