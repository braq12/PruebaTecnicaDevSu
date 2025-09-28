# PruebaTecnicaDevSu


Prueba Técnica – Microservicios (Personas & Cuentas)

Solución compuesta por dos microservicios Spring Boot (Java 21) con Feign para comunicación síncrona y Oracle 21c XE como base de datos.
Se incluye OpenAPI/Swagger para documentación y Spring Actuator para healthchecks.

🏗️ Arquitectura (alto nivel)

servicio-personas (puerto 8085):

CRUD de Persona/Cliente.

Al crear un cliente, si crearCuentaAutomatica=true en el JSON, hace una llamada asíncrona con @Async + Feign al servicio-cuentas para crear una cuenta AHORROS con saldo 0.00.

servicio-cuentas (puerto 8086):

CRU de Cuenta y Movimiento.

Valida la existencia del cliente vía Feign llamando a servicio-personas.

Oracle XE (puerto 1521, PDB XEPDB1):

Dos esquemas: SERVICIO_PERSONAS y SERVICIO_CUENTAS.

Requisitos

Docker y Docker Compose instalados.

JDK 21 (para builds locales).

Maven Wrapper (./mvnw) o Maven 3.9+.

Puertos libres: 1521, 8085, 8086.

Instrucciones:

1. Ejecutar scripts.
sI se desa levantar la bd en docker ejecutar con el docker compose oracle 

docker compose up -d


2. Variables de entorno (archivos .env)
servicio-personas

PUERTO_APP (por defecto 8085)

ORACLE_URL = jdbc:oracle:thin:@//host.docker.internal:1521/XEPDB1 (Windows/Mac)
o jdbc:oracle:thin:@//oracle-xe:1521/XEPDB1 si usas Compose con red compartida

ORACLE_USR = SERVICIO_PERSONAS

ORACLE_PWD = secreto

CUENTAS_URL = http://localhost:8086 (o http://servicio-cuentas:8086 si está en la misma red Docker)

servicio-cuentas

PUERTO_APP (por defecto 8086)

ORACLE_URL = jdbc:oracle:thin:@//host.docker.internal:1521/XEPDB1
o jdbc:oracle:thin:@//oracle-xe:1521/XEPDB1

ORACLE_USR = SERVICIO_CUENTAS

ORACLE_PWD = secreto

PERSONAS_URL = http://localhost:8085 (o http://servicio-personas:8085)
 3. Compilar y levantar ms
 
# En servicio-personas
./mvnw clean package -DskipTests
docker compose up -d --build

# En servicio-cuentas
./mvnw clean package -DskipTests
docker compose up -d --build


Health & Swagger

Personas:

Health: http://localhost:8085/actuator/health

Swagger UI: http://localhost:8085/swagger-ui.html

OpenAPI JSON: http://localhost:8085/v3/api-docs

Cuentas:

Health: http://localhost:8086/actuator/health

Swagger UI: http://localhost:8086/swagger-ui.html

OpenAPI JSON: http://localhost:8086/v3/api-docs


CEjecutar servicios en coleccion postman adjunta

