# 🚀 Challenge Backend - API REST en Spring Boot

Una API REST desarrollada en Java 21 usando Spring Boot, con cálculo dinámico, cacheo de valores externos, historial de llamadas y despliegue completo con Docker.

---

## 📌 Funcionalidades principales

### ✅ 1. Cálculo con porcentaje dinámico
- Endpoint que recibe `num1` y `num2`, los suma y aplica un porcentaje adicional.
- El porcentaje proviene de un servicio externo (mockeado con valor fijo).

### ✅ 2. Caché del porcentaje
- El porcentaje se guarda en memoria (cache) por **30 minutos**.
- Si el servicio externo falla, se usa el valor en caché. Si no existe, devuelve error.

### ✅ 3. Historial de llamadas
- Endpoint que devuelve el historial con:
  - Fecha
  - Endpoint invocado
  - Parámetros
  - Resultado o error
- Registro **asíncrono** en una base de datos PostgreSQL.

---

## ⚙️ Requisitos Técnicos

- 🐘 **Base de datos**: PostgreSQL (contenedor Docker).
- 🐳 **Despliegue**: API + DB orquestados con `docker-compose.yml`.
- 🧪 **Tests**: Unitarios con JUnit y Mockito.
- 📄 **Documentación**: Swagger disponible en `/swagger-ui.html`.
- 📄 **Documentación**: Postman disponible en https://documenter.getpostman.com/view/14868581/2sB2j1hCgG
---

## 🛠️ Tecnologías usadas

- Java 21  
- Spring Boot  
- Spring WebFlux, Spring Data JPA  
- Redis (reactivo, usando Spring Data Redis Reactive)  
- PostgreSQL  
- Docker & Docker Compose  
- JUnit 5 + Mockito  
- Postman y Swagger

---

## 🧑‍💻 Cómo ejecutar este proyecto

### 🔧 Requisitos previos

- Docker y Docker Compose instalados
- (Opcional) Java 21 y Gradle si deseas correrlo sin contenedor

---

### ▶️ Instrucciones

1. Clona el repositorio:
```bash
https://github.com/josephflorian365/ChallengeBackend.git
cd challenge-backend
```
2. Levanta los servicios::
```bash
docker-compose up -d
```
3. Accede a la API:
- 🧩 **API**: [http://localhost:8080](http://localhost:8080)
- 📄 **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🧭 Endpoints principales

| Método | Endpoint     | Descripción                                 |
|--------|--------------|---------------------------------------------|
| POST    | `/sum` | Realiza suma con porcentaje dinámico        |
| GET    | `/history`   | Devuelve historial de llamadas (paginado)   |

---

## 📌 Ejemplos de Uso

### 🟢 POST `/sum`

Realiza una suma entre dos números y aplica un porcentaje adicional.

- **URL:** `http://localhost:8080/api/sum`
- **Método:** `POST`
- **Query param (opcional):** `mockEnabled=true`
- **Request Body:**

```json
{
  "num1": 10,
  "num2": 5
}
```
### 🔵 GET `/history`

Devuelve el historial de llamadas realizadas al endpoint `/sum`, con soporte para paginación.

- **URL base:** `http://localhost:8080/api/history`
- **Método:** `GET`
- **Parámetros de consulta (query params):**
  - `page` (int): Número de página (por defecto: `1`)
  - `size` (int): Cantidad de resultados por página (por defecto: `10`)

#### 📘 Ejemplo de solicitud

```bash
curl "http://localhost:8080/api/history?page=1&size=10"
```

## 🧭 Endpoints secundarios
| Método | Endpoint                                     | Descripción                                 |
|--------|----------------------------------------------|---------------------------------------------|
| POST   | `/sum?mockEnabled=true`                | Realiza suma con porcentaje estático de 10% añadido        |
| GET    | `/history?page=1&size=10`                    | Devuelve historial de llamadas (paginado dinámco)   |

---

## 📦 Despliegue con Docker

El archivo `docker-compose.yml` incluye:

- API (`setiembre/pee-java_app`)
- PostgreSQL
- Redis

La imagen está disponible en Docker Hub:  
👉 [`setiembre/pee-java_app`](https://hub.docker.com/r/setiembre/pee-java_app)

---

## 📚 Arquitectura del proyecto

src ├── controller ├── service ├── repository ├── config ├── model └── util └── webclient └── advice

yaml
Copiar
Editar

Diseño basado en capas: separación de responsabilidades, código limpio y arquitectura reactiva con Spring WebFlux.

---

## ✅ Checklist técnico

- [x] Spring WebFlux + controladores reactivos
- [x] Caché con Redis (TTL 30 min)
- [x] PostgreSQL
- [x] Registro asíncrono no bloqueante
- [x] Docker + docker-compose
- [x] Documentación con Postman y Swagger
- [x] Tests unitarios con simulación de errores

---

## 👨‍💻 Autor

Desarrollado por **Joseph365** como parte de un challenge técnico.
