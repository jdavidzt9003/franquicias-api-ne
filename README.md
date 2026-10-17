# API Franquicias

API REST para administrar **franquicias**, sus **sucursales** y los **productos** (con stock) de cada sucursal.

## Stack

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 3.3 | Framework (Web, Data JPA, Validation, Actuator) |
| PostgreSQL 16 | Persistencia |
| Docker / Docker Compose | Empaquetado y ejecución |
| springdoc-openapi | Documentación Swagger UI |
| JUnit 5 + Mockito | Pruebas unitarias |

## Modelo de datos

```
Franchise (id, name)
  └── Branch (id, name, franchise_id)
        └── Product (id, name, stock, branch_id)
```

Las tablas se crean automáticamente al iniciar la aplicación (`spring.jpa.hibernate.ddl-auto=update`).
Para un entorno productivo se recomienda cambiarlo a `validate` y manejar el esquema con Flyway/Liquibase.

## Cómo ejecutar

### Opción 1: Docker Compose (recomendada)

Requiere únicamente Docker.

```bash
docker compose up --build
```

Levanta PostgreSQL (puerto **5433** del host, para no chocar con un PostgreSQL local) y la API en `http://localhost:8080`.

Conexión desde DBeaver u otro cliente: host `localhost`, puerto `5433`, base `franquicias`, usuario `franquicias`, clave `franquicias`.

Para detener y borrar los contenedores: `docker compose down` (agrega `-v` para borrar también los datos).

### Opción 2: Local con Maven

Requiere Java 21, Maven y un PostgreSQL disponible.

```bash
# Solo la base de datos con Docker
docker compose up -d db   # expone PostgreSQL en el puerto 5433 del host

# La API (apuntando al puerto 5433)
export DB_URL=jdbc:postgresql://localhost:5433/franquicias
mvn spring-boot:run
```

### Variables de entorno

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/franquicias` |
| `DB_USER` | `franquicias` |
| `DB_PASSWORD` | `franquicias` |
| `DDL_AUTO` | `update` |
| `PORT` | `8080` |

Para usar un PostgreSQL en la nube (RDS, Cloud SQL, Neon, Supabase, etc.) basta con definir `DB_URL`, `DB_USER` y `DB_PASSWORD`.

## Documentación interactiva

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health

## Endpoints

Base: `/api/franchises`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/franchises` | Crear franquicia |
| PATCH | `/api/franchises/{franchiseId}` | Actualizar nombre de la franquicia (extra) |
| POST | `/api/franchises/{franchiseId}/branches` | Agregar sucursal a una franquicia |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}` | Actualizar nombre de la sucursal (extra) |
| POST | `/api/franchises/{franchiseId}/branches/{branchId}/products` | Agregar producto a una sucursal |
| DELETE | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar producto de una sucursal |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Modificar el stock de un producto |
| GET | `/api/franchises/{franchiseId}/top-stock-products` | Producto con más stock por sucursal de la franquicia |

### Ejemplos con curl

```bash
# 1. Crear franquicia
curl -X POST localhost:8080/api/franchises \
  -H 'Content-Type: application/json' \
  -d '{"name":"Burger House"}'
# -> {"id":1,"name":"Burger House"}

# 2. Agregar sucursal
curl -X POST localhost:8080/api/franchises/1/branches \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sucursal Centro"}'
# -> {"id":1,"name":"Sucursal Centro","franchiseId":1}

# 3. Agregar productos
curl -X POST localhost:8080/api/franchises/1/branches/1/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Hamburguesa","stock":40}'

curl -X POST localhost:8080/api/franchises/1/branches/1/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Papas","stock":75}'

# 4. Modificar stock
curl -X PATCH localhost:8080/api/franchises/1/branches/1/products/1/stock \
  -H 'Content-Type: application/json' \
  -d '{"stock":120}'

# 5. Producto con más stock por sucursal
curl localhost:8080/api/franchises/1/top-stock-products
# -> [{"branchId":1,"branchName":"Sucursal Centro","productId":1,"productName":"Hamburguesa","stock":120}]

# 6. Eliminar producto
curl -X DELETE localhost:8080/api/franchises/1/branches/1/products/2

# 7. Renombrar franquicia y sucursal
curl -X PATCH localhost:8080/api/franchises/1 \
  -H 'Content-Type: application/json' -d '{"name":"Burger House Colombia"}'

curl -X PATCH localhost:8080/api/franchises/1/branches/1 \
  -H 'Content-Type: application/json' -d '{"name":"Sucursal Chapinero"}'
```

### Códigos de respuesta

| Código | Cuándo |
|---|---|
| 200 | Actualización o consulta exitosa |
| 201 | Recurso creado |
| 204 | Producto eliminado |
| 400 | Datos inválidos (nombre vacío, stock negativo o nulo, JSON mal formado) |
| 404 | Franquicia, sucursal o producto inexistente (o que no pertenece a la ruta indicada) |

Formato de error:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Franquicia no encontrada: 99",
  "timestamp": "2026-10-16T19:42:00Z"
}
```

## Decisiones de diseño

- **Rutas jerárquicas**: cada operación valida que la sucursal pertenezca a la franquicia y el producto a la sucursal, evitando modificar recursos de otra franquicia por error.
- **Consulta de mayor stock**: se resuelve en una sola consulta JPQL con subconsulta (`max(stock)` por sucursal), sin cargar toda la jerarquía en memoria. Si en una sucursal hay **empate** en el stock máximo, se devuelven todos los productos empatados. Las sucursales sin productos no aparecen en el resultado.
- **DTOs como `record`** separados de las entidades, con validaciones de Bean Validation.
- **Manejo de errores centralizado** con `@RestControllerAdvice`.
- **Dockerfile multi-etapa**: compila con Maven y ejecuta sobre una imagen JRE liviana con usuario no root.

## Pruebas

```bash
mvn test
```

## Estructura

```
src/main/java/com/franquicias/api
├── web
│   ├── controller   # Endpoints REST
│   └── exception    # Manejo global de errores
├── domain
│   ├── dto          # Requests / responses (records)
│   ├── exception    # Excepciones de negocio
│   ├── repository   # Contratos (interfaces puras, sin Spring Data)
│   └── service      # Lógica de negocio
└── persistence
    ├── entity       # Entidades JPA
    ├── crud         # Repositorios Spring Data JPA
    └── *Impl        # Implementan los contratos del dominio usando los crud
```

Los repositorios de `domain` son interfaces simples; su implementación vive en `persistence`
y delega en Spring Data. Así el dominio no depende de JPA.

## Git

```bash
git init
git add .
git commit -m "feat: API de franquicias con Spring Boot y PostgreSQL"
```
