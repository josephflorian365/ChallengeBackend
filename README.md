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
- Spring Web, Spring Cache, Spring Data JPA
- PostgreSQL
- Docker & Docker Compose
- JUnit 5 + Mockito
- Swagger

---

## 🧑‍💻 Cómo ejecutar este proyecto

### 🔧 Requisitos previos

- Docker y Docker Compose instalados
- (Opcional) Java 21 y Maven si deseas correrlo sin contenedor

---

### ▶️ Instrucciones

1. Clona el repositorio:
```bash
https://github.com/josephflorian365/ChallengeBackend.git
cd challenge-backend
cd reto
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
| POST    | `/calculate` | Realiza suma con porcentaje dinámico        |
| GET    | `/history`   | Devuelve historial de llamadas (paginado)   |

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

src ├── controller ├── service ├── repository ├── config ├── model └── util

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
- [x] Swagger y documentación con Postman
- [x] Tests unitarios con simulación de errores

---

## 👨‍💻 Autor

Desarrollado por **Joseph365** como parte de un challenge técnico.
