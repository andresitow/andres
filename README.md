# Inventario Supermercado — API REST (Spring Boot + MySQL)

Backend de gestión de inventario de supermercado construido con **Spring Boot 3**
(Java 17) y **MySQL** mediante Spring Data JPA. Expone una API REST para
registrar productos, mover stock y consultar valorización y alertas.

El modelo usa herencia polimórfica: `Producto` es la clase base y
`ProductoPerecedero`, `ProductoNoPerecedero` y `ProductoFruver` sobrescriben el
cálculo del precio final (descuento por vencimiento, IVA 19%, recargo orgánico).
Todas las subclases se guardan en una sola tabla (`productos`) con una columna
discriminadora.

## Requisitos

- JDK 17 o superior
- Maven 3.9+ (o el wrapper `./mvnw`)
- MySQL 8.x en ejecución, o Docker para levantarlo con el `docker-compose.yml` incluido

## 1. Levantar la base de datos MySQL

La forma más simple es con Docker:

```bash
docker compose up -d
```

Esto crea un MySQL 8.4 en `localhost:3306` con:

- Base de datos: `inventario`
- Usuario: `inventario` / contraseña: `inventario`

Si ya tienes tu propio MySQL, crea la base de datos y ajusta las credenciales
(ver sección de configuración). La aplicación crea la base si no existe
(`createDatabaseIfNotExist=true`) y genera la tabla automáticamente con Hibernate.

## 2. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O empaquetando el jar:

```bash
mvn clean package
java -jar target/inventario-floci-1.0.0.jar
```

La API queda en `http://localhost:8080`. Al arrancar con la tabla vacía se
cargan 6 productos de ejemplo.

## 3. Verificar la conexión a la base de datos

```bash
curl http://localhost:8080/api/health
```

Respuesta esperada cuando la conexión a MySQL es exitosa:

```json
{ "aplicacion": "UP", "baseDatos": "UP", "mysqlVersion": "8.4.13" }
```

## Configuración

Los parámetros están en `src/main/resources/application.properties` y se pueden
sobreescribir con variables de entorno:

| Variable      | Valor por defecto                                          |
|---------------|------------------------------------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/inventario?createDatabaseIfNotExist=true&serverTimezone=UTC` |
| `DB_USER`     | `inventario`                                               |
| `DB_PASSWORD` | `inventario`                                               |
| `SERVER_PORT` | `8080`                                                     |

Ejemplo con credenciales propias:

```bash
DB_URL="jdbc:mysql://localhost:3306/mi_bd" DB_USER=root DB_PASSWORD=secreto mvn spring-boot:run
```

## Endpoints

| Método | Ruta                                 | Descripción                                   |
|--------|--------------------------------------|-----------------------------------------------|
| GET    | `/api/health`                        | Estado de la app y de la conexión a MySQL     |
| GET    | `/api/productos`                     | Lista todos los productos                     |
| GET    | `/api/productos/{id}`                | Obtiene un producto por id                    |
| POST   | `/api/productos`                     | Crea un producto                              |
| POST   | `/api/productos/{id}/entradas`       | Suma stock (entrada de mercancía)             |
| POST   | `/api/productos/{id}/salidas`        | Descuenta stock (venta)                       |
| DELETE | `/api/productos/{id}`                | Elimina un producto                           |
| GET    | `/api/productos/stock-critico`       | Productos con stock por debajo del umbral (5) |
| GET    | `/api/productos/proximos-a-vencer`   | Perecederos a 3 días o menos de caducar       |
| GET    | `/api/productos/valorizacion`        | Valor total del inventario                    |

### Ejemplos

Crear un producto no perecedero:

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"tipo":"NO_PERECEDERO","id":"N10","nombre":"Jabón","precioBase":3000,"stock":10,"codigoBarras":"9999","categoriaPasillo":"Aseo","pesoNetoKg":0.5}'
```

Crear un perecedero:

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"tipo":"PERECEDERO","id":"P10","nombre":"Leche","precioBase":3800,"stock":12,"codigoBarras":"7701","fechaCaducidad":"2025-12-31","requiereRefrigeracion":true}'
```

Registrar una salida (venta) de 3 unidades:

```bash
curl -X POST http://localhost:8080/api/productos/N10/salidas \
  -H "Content-Type: application/json" \
  -d '{"cantidad":3}'
```

## Estructura del proyecto

```
src/main/java/com/supermercado/inventario
├── InventarioApplication.java      # Punto de entrada Spring Boot
├── model/                          # Entidades JPA (herencia SINGLE_TABLE)
├── repository/                     # ProductoRepository (Spring Data JPA)
├── service/                        # InventarioService (lógica de negocio)
├── controller/                     # API REST, health y manejo de errores
├── dto/                            # Objetos de entrada/salida de la API
├── exception/                      # Excepciones de negocio
└── config/                         # Carga de datos demo al arrancar
```

## Pruebas

```bash
mvn test
```

Los tests usan H2 en memoria (perfil `test`), así que no requieren MySQL.

## Tipos de producto y precio final

| Tipo             | Regla de precio final                              |
|------------------|----------------------------------------------------|
| `PERECEDERO`     | 30% de descuento si faltan 3 días o menos para caducar |
| `NO_PERECEDERO`  | Precio base + IVA 19%                              |
| `FRUVER`         | Precio por kilo × peso, +10% si es orgánico        |
