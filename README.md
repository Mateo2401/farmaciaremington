# Inventario de farmacia

Aplicación web para administrar productos, categorías, proveedores, usuarios, ventas y movimientos de inventario. Está desarrollada con Java 17 y Spring Boot, utiliza Thymeleaf para las vistas, Spring Security para autenticación y autorización, y JDBC para la persistencia del catálogo de productos.

> Estado documentado al 7 de noviembre de 2026. La documentación refleja el código actual; no implica que todos los módulos ya persistan en MySQL.

## Tecnologías

- Java 17
- Spring Boot 4.1.2
- Spring MVC y Thymeleaf
- Spring Security
- Spring JDBC (`JdbcTemplate`)
- MySQL Connector/J
- JUnit 5, Mockito y MockMvc
- Maven Wrapper

## Módulos

- Productos: MVC y API REST; el DAO de productos consulta y modifica la tabla `producto` mediante SQL.
- Categorías, proveedores, usuarios, ventas, detalles y movimientos: controladores y vistas/API disponibles, pero sus implementaciones DAO actuales almacenan datos en memoria. Esos datos no son persistentes y se pierden al reiniciar.
- Autenticación: usuarios de demostración en memoria, no cargados desde la tabla `usuario`.
- Dashboard: muestra información de bienvenida y estadísticas de ejemplo; los totales todavía no se consultan de MySQL.

Las páginas de listas distintas de productos contienen actualmente filas demostrativas estáticas. Por eso, que una operación responda correctamente en esas páginas no significa que se haya guardado en la base de datos.

## Estructura

```text
src/
  main/
    java/com/uniremingto/inventario/
      config/                 Configuración de seguridad
      controller/             Login y dashboard
        mvc/                  Controladores para vistas Thymeleaf
        res/                  Controladores REST
      model/
        dao/                  Interfaces e implementaciones DAO
        vo/                   Objetos de transferencia de datos
    resources/
      static/css/             Estilos
      static/js/              JavaScript de interfaz
      templates/              Vistas Thymeleaf
      application.properties  Configuración de conexión
  test/java/com/uniremingto/inventario/
    controller/mvc/           Pruebas MockMvc de controladores MVC
    controller/res/           Pruebas MockMvc de controladores REST
```

## Requisitos

- JDK 17 instalado y `JAVA_HOME` configurado.
- MySQL 8.0 o compatible disponible para las operaciones de productos.
- Base de datos `farmacia_inventario` y las tablas creadas con el esquema del negocio.
- Credenciales MySQL con permisos de lectura, inserción, actualización y eliminación en la tabla `producto`.

Este repositorio no incluye actualmente un archivo `schema.sql` ni una migración. Ejecuta el script SQL del proyecto en MySQL antes de usar las operaciones de productos.

## Configuración de MySQL

La configuración está en `src/main/resources/application.properties`. Por defecto intenta conectarse a:

```text
URL:      jdbc:mysql://localhost:3306/farmacia_inventario?useSSL=false&serverTimezone=UTC
Usuario:  root
Clave:    vacía
```

Personaliza la conexión mediante variables de entorno; no guardes credenciales reales en el repositorio:

```text
DB_URL=jdbc:mysql://localhost:3306/farmacia_inventario?useSSL=false&serverTimezone=UTC
DB_USERNAME=usuario_mysql
DB_PASSWORD=clave_mysql
```

En PowerShell, define las variables en la terminal que utilizarás para iniciar la aplicación, o en la configuración de ejecución de VS Code. El proceso de Spring Boot debe reiniciarse después de cambiar la configuración.

## Ejecutar

Desde la raíz del proyecto en Windows:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/farmacia_inventario?useSSL=false&serverTimezone=UTC"
$env:DB_USERNAME = "usuario_mysql"
$env:DB_PASSWORD = "clave_mysql"
.\mvnw.cmd spring-boot:run
```

La aplicación normalmente queda disponible en `http://localhost:8080`.

## Acceso de demostración

La configuración actual declara estas cuentas en memoria:

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | `admin@farmacia.com` | `admin123` |
| Vendedor | `vendedor@farmacia.com` | `vendedor123` |

Son credenciales locales de demostración, no adecuadas para producción. La contraseña de la tabla de usuarios todavía no participa en el inicio de sesión.

## Vistas MVC

| Ruta | Función |
|---|---|
| `/login` | Inicio de sesión |
| `/dashboard` | Panel principal |
| `/productos` | Lista de productos desde el DAO |
| `/productos/nuevo` | Formulario de alta |
| `/productos/{id}/editar` | Formulario de edición |
| `/categorias`, `/proveedores`, `/usuarios`, `/ventas` | Vistas de gestión |
| `/detalle-ventas`, `/movimientos-inventario` | Detalles de venta y movimientos |

El alta MVC de productos envía `POST /productos`; la edición envía `POST /productos/{id}`; la eliminación envía `POST /productos/{id}/eliminar`. La edición y la eliminación tienen en cuenta las restricciones de claves foráneas del esquema.

## API REST

Los controladores REST exponen operaciones CRUD bajo estos prefijos:

| Recurso | Prefijo |
|---|---|
| Categorías | `/api/categorias` |
| Proveedores | `/api/proveedores` |
| Usuarios | `/api/usuarios` |
| Productos | `/api/productos` |
| Ventas | `/api/ventas` |
| Detalles de venta | `/api/detalle-ventas` |
| Movimientos | `/api/movimientos-inventario` |

En general, los endpoints usan `GET /{recurso}`, `GET /{recurso}/{id}`, `POST /{recurso}`, `PUT /{recurso}/{id}` y `DELETE /{recurso}/{id}`. Los `POST` responden `201 Created`, los `PUT` `200 OK`, los `DELETE` exitosos `204 No Content` y las búsquedas por ID inexistentes `404 Not Found`.

Las rutas requieren autenticación según la configuración actual. Las pruebas unitarias de controlador no cargan los filtros de Spring Security ni prueban CSRF.

## Pruebas

Ejecuta la suite con:

```powershell
.\mvnw.cmd test
```

Las pruebas de `controller/mvc` y `controller/res` usan MockMvc y Mockito: verifican rutas, binding de formularios/JSON, estados HTTP y delegación a los DAO simulados. No conectan a MySQL ni validan SQL real. El test `InventarioApplicationTests` comprueba la carga del contexto.

## Generar Javadoc

El proyecto incluye el plugin Maven de Javadoc. Genera la documentación HTML con:

```powershell
.\mvnw.cmd javadoc:javadoc
```

El resultado se escribe en `target/site/apidocs/index.html`. Requiere JDK 17 y genera la documentación de la API Java; no reemplaza las pruebas ni la configuración de la base de datos.

## Limitaciones conocidas

- Solo `ProductoDAOImpl` es persistente y usa MySQL. Los demás DAO (`Categoria`, `Proveedor`, `Usuario`, `Venta`, `DetalleVenta` y `MovimientoInventario`) usan mapas en memoria.
- La autenticación y los roles también están definidos en memoria.
- No se incluyen migraciones ni inicialización automática del esquema.
- El dashboard y varias listas contienen cifras o registros de muestra.
- Las pruebas con Mockito/MockMvc no demuestran conectividad ni persistencia real de MySQL.
- Antes de producción deben implementarse persistencia para todos los módulos, gestión segura de usuarios/roles, validaciones y pruebas de integración con una base de pruebas.
