# ms-compra — Microservicio de Compras y Abastecimiento

Microservicio autónomo para la gestión integral de órdenes de compra a proveedores de la Pastelería "Las Melosas". Permite registrar órdenes de compra con cálculo automático de totales y costo de transporte, actualizar estados de compra y listar adquisiciones por proveedor.

## Tecnologías y Características
- **Java 17** & **Spring Boot 4.1.1**
- **Arquitectura Desacoplada**: Sin dependencias de Service Discovery (Eureka) ni API Gateway.
- **Autenticación Descentralizada**: Validación de tokens JWT firmados con clave HMAC-SHA compartida (`JWT_SECRET`).
- **Base de Datos**: MySQL 8.0 en contenedores y H2 en memoria para pruebas automáticas.
- **Migraciones Flyway**: Esquema versionado con validación estricta de entidades JPA.
- **MapStruct 1.5.5.Final**: Mapeo estricto entre entidades y DTOs sin exponer el modelo de datos.

## Puertos y Red
- **Puerto de la Aplicación**: `8085`
- **Puerto MySQL (Host)**: `3310`
- **Nombre de Base de Datos**: `compradb`
- **Red Docker**: `pasteleria-network` (bridge externa)

## Endpoints Principales (`/api/v1/compras`)
| Método | Endpoint | Roles Permitidos | Descripción |
|---|---|---|---|
| `GET` | `/api/v1/compras` | ADMIN, VENDEDOR_SUPERVISOR | Listado general con filtro opcional `?estado=` |
| `GET` | `/api/v1/compras/{id}` | ADMIN, VENDEDOR_SUPERVISOR | Detalle completo de una orden de compra |
| `GET` | `/api/v1/compras/proveedor/{rut}` | ADMIN, VENDEDOR_SUPERVISOR | Historial de compras por RUT de proveedor |
| `POST` | `/api/v1/compras` | ADMIN, VENDEDOR_SUPERVISOR | Registrar una nueva orden de compra (201 Created) |
| `PATCH` | `/api/v1/compras/{id}/estado` | ADMIN, VENDEDOR_SUPERVISOR | Cambiar estado (`RECIBIDA`, `CANCELADA`) |
| `DELETE` | `/api/v1/compras/{id}` | ADMIN | Eliminar compra en estado PENDIENTE o CANCELADA (204) |

## Ejecución Local
```bash
# Configurar variables de entorno o crear .env
mvn clean test
mvn spring-boot:run
```

## Ejecución con Docker
```bash
# Crear la red compartida (si no existe)
docker network create pasteleria-network

# Levantar MySQL y ms-compra
docker compose up --build -d
```
