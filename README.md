# Junior Store Chatbot

API REST y chatbot conversacional para la tienda oficial de Junior de Barranquilla. Los hinchas pueden consultar productos, precios y disponibilidad a través de un bot de Telegram impulsado por un agente de IA.

---

## Tecnologías

**Backend (API)**
- Java 21
- Spring Boot 3.3 (Web, Data JPA, Validation)
- H2 (base de datos en memoria)
- Lombok
- Maven

**Orquestación del chatbot**
- n8n (plataforma de automatización con IA)
- LLM: OpenAI GPT-4o-mini (modelo del agente)
- Telegram Bot API (interfaz de usuario)

**Infraestructura**
- Docker + Docker Compose
- ngrok (túnel HTTPS público para desarrollo)

---

## Arquitectura

Monolito en capas, organizado por feature, expuesto vía REST y consumido por un agente de IA.

```
┌──────────┐    ┌─────────┐    ┌─────────────────┐    ┌──────────────┐
│  Usuario │───▶│Telegram │───▶│ n8n (AI Agent)  │───▶│  Spring API  │
│          │◀───│   Bot   │◀───│ + LLM + Tools   │◀───│  (catálogo)  │
└──────────┘    └─────────┘    └─────────────────┘    └──────┬───────┘
                                                             │
                                                       ┌─────▼─────┐
                                                       │  H2 (in   │
                                                       │  memory)  │
                                                       └───────────┘
```

**Capas de la API (Spring Boot):**

```
com.carinosas.juniorn8napi.product
├── controller    ← endpoints REST
├── service       ← lógica de negocio (low-stock, filtros)
├── repository    ← acceso a datos (Spring Data JPA)
├── domain        ← entidad Product + enums (Size, ProductType)
├── dto           ← contratos de respuesta JSON
├── mapper        ← entidad → DTO (campos derivados)
└── exception     ← manejo global de errores
```

Flujo de datos: `Controller → Service → Repository → DB`. El `Mapper` convierte entidades a DTOs antes de responder.

---

## Modelo de dominio

Entidad única `Product`:

| Campo  | Tipo          | Notas                                       |
|--------|---------------|---------------------------------------------|
| id     | Long          | Auto-generado                               |
| name   | String        | "Camiseta Local 2025"                       |
| type   | ProductType   | Enum (jersey local, gorra, peluche, etc.)   |
| size   | Size          | S, M, L, XL, None                           |
| price  | BigDecimal    | COP, precisión 2 decimales                  |
| stock  | Integer       | Cantidad en inventario                      |

**Campos derivados** (calculados, no almacenados):
- `available` = `stock > 0`
- `lowStock` = `stock < 20`

---

## Endpoints

| Método | Ruta                                       | Descripción                          |
|--------|--------------------------------------------|--------------------------------------|
| GET    | `/api/products`                            | Catálogo completo                    |
| GET    | `/api/products/available`                  | Solo productos en stock              |
| GET    | `/api/products/{id}`                       | Detalle por ID                       |
| GET    | `/api/products/search?name=&type=&size=`   | Búsqueda con filtros opcionales      |

API de solo lectura — no se procesan compras desde el bot.

### Ejemplo de respuesta

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

---

## Decisiones de diseño

- **Monolito en capas:** suficiente para el alcance, fácil de evolucionar.
- **DTOs separados de entidades:** desacopla el contrato HTTP del esquema de BD.
- **`available` y `lowStock` derivados, no almacenados:** evita inconsistencias.
- **`BigDecimal` para precios:** precisión exacta (nunca `float`/`double` para dinero).
- **Enums con `@Enumerated(EnumType.STRING)`:** evita corrupción de datos al reordenar valores.
- **Solo endpoints GET:** seguridad por diseño — el bot no puede modificar inventario.
- **H2 en memoria + `data.sql`:** cada arranque carga datos de prueba consistentes.

---

## Cómo ejecutar

### Requisitos
- Docker Desktop
- Cuenta gratuita de ngrok
- Token de OpenAI (o Anthropic / Gemini)
- Bot creado con `@BotFather` en Telegram

### 1. Iniciar los contenedores

Crear archivo `.env` en la raíz:

```env
N8N_USER=admin
N8N_PASSWORD=changeme123
```

Construir e iniciar:

```bash
docker compose up --build
```

Servicios disponibles:
- API: http://localhost:8080/api/products
- n8n: http://localhost:5678

### 2. Exponer n8n con ngrok (para Telegram)

Telegram requiere HTTPS público. En otra terminal:

```bash
ngrok http 5678
```

Copiar la URL `https://...ngrok-free.app` y añadirla a `docker-compose.yml`:

```yaml
n8n:
  environment:
    N8N_HOST: <hostname-ngrok>
    N8N_PROTOCOL: https
    WEBHOOK_URL: https://<hostname-ngrok>/
```

Reiniciar: `docker compose down && docker compose up -d`

### 3. Configurar el flujo en n8n

En `http://localhost:5678`:

1. Construir el workflow:
    - **Telegram Trigger** → **AI Agent** → **Telegram Send Message**
    - Sub-nodos del agente: Chat Model (OpenAI), Memory (Simple), 3 HTTP Tools
2. Cargar credenciales de OpenAI y Telegram
3. Activar el workflow

### 4. Probar el bot

Buscar el bot en Telegram y enviar mensajes:

- *"¿Qué productos tienen?"*
- *"¿Tienen camiseta local talla M?"*
- *"¿Cuánto cuesta el peluche del tiburón?"*

---

## Estructura del proyecto

```
JuniorN8NApi/
├── src/main/
│   ├── java/com/carinosas/juniorn8napi/
│   │   └── product/   (controller, service, repository, domain, dto, mapper, exception)
│   └── resources/
│       ├── application.yml
│       ├── application-docker.yml
│       └── data.sql
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env
└── README.md
```

---

## Datos de prueba

`data.sql` carga productos al arrancar: camisetas locales/visitantes/alternativas en varias tallas, pantalonetas, medias, gorras, peluches del tiburón y camisetas para perros. Incluye productos agotados, con stock bajo y disponibles para demostrar todos los estados del bot.

---

## Limitaciones conocidas (alcance de mockup)

- Datos en memoria → se reinician en cada arranque
- Sin autenticación
- Sin endpoints de compra (fuera de alcance)
- ngrok con URL temporal (cambia al reiniciar en plan gratuito)
- Sin tests automatizados