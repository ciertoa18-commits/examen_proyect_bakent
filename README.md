# EquipAndes - AlquileresBackend

API REST para alquiler de equipos de sonido, proyección e iluminación. Proyecto independiente del ejemplo ClinicaBackend, con la misma separación por capas. Implementa RF-01 a RF-07, RN-01 a RN-04 y las ampliaciones SC-A, SC-B, SC-C y SC-D.

## 1. Requisitos y apertura

- JDK 21. El pom fija release 21; un JDK superior también puede compilarlo.
- Maven 3.9 o el wrapper incluido.
- Oracle local con un esquema dedicado y permiso para crear tablas e identidades.
- IntelliJ: abrir ESTA carpeta como proyecto Maven, seleccionar JDK 21 y esperar la sincronización del pom.
- SQL Developer, SQLcl o SQL*Plus para cargar la semilla.
- Postman para ejecutar el Anexo B.

## 2. Configurar Oracle

El perfil dev usa por defecto localhost:1522/FREEPDB1 con el usuario EquipAndess y la contraseña de desarrollo 1234567, de modo que arranca sin cambios manuales (Run de AlquileresBackendApplication en IntelliJ). Las variables DB_URL, DB_USERNAME y DB_PASSWORD permiten cambiarlos. El perfil prod no tiene valores por defecto: exige las variables de entorno.

En PowerShell, dentro de AlquileresBackend:

~~~powershell
$env:DB_URL = 'jdbc:oracle:thin:@localhost:1522/FREEPDB1'
$env:DB_USERNAME = 'EquipAndess'
$claveOracle = Read-Host 'Contraseña de Oracle' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $claveOracle).Password
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=dev'
~~~

Como alternativa, ejecutar .\iniciar.ps1 -Url 'jdbc:oracle:thin:@localhost:1522/FREEPDB1' -Usuario 'EquipAndess'; solicita la contraseña sin mostrarla. El archivo .env.example solo documenta las variables y no se carga automáticamente.

En IntelliJ, agregar DB_URL, DB_USERNAME y DB_PASSWORD en las variables de entorno de la configuración de ejecución de AlquileresBackendApplication. Activar dev. Nunca usar el esquema de la clínica para esta demostración: usar un esquema de alquileres propio.

Si el usuario todavía no existe, el DBA debe crearlo en la PDB correcta con CREATE SESSION, CREATE TABLE, CREATE SEQUENCE y cuota en su tablespace. No hace falta dar permisos DBA.

## 3. Primer arranque y datos

1. Arrancar con dev; Hibernate crea las cinco tablas y sus identidades.
2. Abrir datos_semilla.sql en una conexión al MISMO usuario y servicio.
3. Ejecutar el script completo con F5 en SQL Developer. Incluye 3 categorías, 12 equipos y 6 clientes; EQP-004 y EQP-008 sin stock y EQP-011 inactivo.
4. Ejecutarlo una sola vez sobre tablas vacías. No contiene borrados ni reemplaza datos existentes.
5. Consultar GET /api/v1/categorias, /equipos y /clientes; los IDs no se deben asumir.
6. Abrir http://localhost:8080/swagger-ui.html y comprobar el título Alquileres API, versión v1.
7. GET http://localhost:8080/api/v1/health debe informar database UP.

Para repetir exactamente el Anexo B se necesita un esquema de demostración recién cargado. No reutilizar el stock modificado por una ejecución anterior. La colección resuelve los IDs por código y calcula las fechas de inicio con America/Lima.

## 4. Pruebas automatizadas

~~~powershell
.\mvnw.cmd test
.\mvnw.cmd package
~~~

Las pruebas usan exclusivamente H2 en src/test/resources/application-test.yaml, aisladas de Oracle, con ORM, transacciones y peticiones MockMvc reales. Incluyen descuento semanal, rollback de cabecera/detalles/stock, devoluciones repetidas, máximo de alquileres, duplicados, fechas, validaciones, búsquedas, CORS, cambios SC, reportes y contrato OpenAPI. Además comprueban health DOWN/503 y la respuesta genérica 500.

H2 solo tiene alcance test en Maven. dev y prod siempre usan Oracle. Estas pruebas no sustituyen la demostración ni las capturas de Oracle.

## 5. Postman: Anexo B completo

1. Importar postman/EquipAndes.postman_collection.json y postman/Local.postman_environment.json.
2. Elegir EquipAndes local.
3. Con la semilla intacta, ejecutar con Collection Runner SOLO la carpeta Anexo B, en orden, sin ejecución paralela.
4. Las peticiones 00 resuelven categorías, códigos de equipos y clientes. CP01 a CP16 contienen aserciones HTTP y de contenido; las comprobaciones auxiliares verifican stock y rollback.
5. CP06 espera 2190.00; CP10 espera subtotal 1680.00, descuento 252.00 y total 1428.00. CP14 comprueba el orden y los importes exactos.
6. CP15 devuelve el alquiler principal y rechaza la segunda devolución con 409.
7. CP16 permite el preflight desde 4200 y rechaza 3000. Usar Postman Desktop o su agente para los encabezados Origin.
8. Después se puede ejecutar la carpeta Parte II, que también modifica stock y reportes.
9. La carpeta Manual - indisponibilidad Oracle se ejecuta aparte: primero detener Oracle manteniendo la API abierta, comprobar 503, volver a iniciar Oracle y comprobar 200. No incluirla en una ejecución normal de toda la colección.

## 6. Endpoints

| Recurso | Operaciones |
| --- | --- |
| /api/v1/health | GET, conexión real con SELECT 1 FROM DUAL, 200/503 |
| /api/v1/categorias | POST, GET; /{id}: GET, PUT, DELETE |
| /api/v1/categorias/{id}/equipos | GET |
| /api/v1/equipos | POST, GET; /{id}: GET, PUT, DELETE |
| /api/v1/equipos/buscar | GET: nombre, categoriaId, tarifaMax, disponible, orden, dir, condicion |
| /api/v1/clientes | POST, GET; /{id}: GET, PUT, DELETE |
| /api/v1/clientes/{id}/alquileres | GET, estado opcional (SC-A) |
| /api/v1/alquileres | POST, GET; /{id}: GET |
| /api/v1/alquileres/{id}/devolver | PATCH |
| /api/v1/alquileres/{id}/equipos/{equipoId} | DELETE, devuelve alquiler recalculado (SC-B) |
| /api/v1/reportes/equipos-alquilados | GET, periodo obligatorio AAAA-MM, categoriaId opcional |
| /api/v1/reportes/ingresos-por-categoria | GET, periodo obligatorio AAAA-MM (SC-C) |

Orden: nombre, tarifa o unidades; dir: asc o desc. disponible=true significa stock > 0 y false significa stock = 0. Los equipos inactivos pueden aparecer en consultas de catálogo: la regla que impide alquilarlos se aplica al registrar el alquiler. condicion admite NUEVO, BUENO o REGULAR y es obligatoria en POST/PUT de equipos (SC-D).

POST responde 201 y Location; GET/PUT/PATCH responden 200; DELETE de catálogo responde 204; DELETE SC-B responde 200 con el alquiler recalculado. No se elimina una categoría con equipos, un equipo con detalles históricos ni un cliente con alquileres. No existe edición o eliminación general del alquiler porque el contrato pide registro y devolución.

Errores uniformes: timestamp, status, error, message, path y validationErrors. Bean Validation devuelve 400 con campos; recurso o referencia ausente devuelve 404; reglas y asociaciones devuelven 409; fallos inesperados devuelven 500 sin detalles internos. health tiene su propio DTO tanto en 200 como en 503.

## 7. Estructura

~~~text
src/main/java/pe/edu/upeu/AlquileresBackend/
  config/                 CORS, OpenAPI y Clock America/Lima
  controller/             HTTP y DTO; sin reglas de negocio
  dto/                    Record de entrada y salida por recurso
    reporte/              Proyecciones JPQL
  entity/                 Cinco entidades y Auditable
  enums/                  EstadoAlquiler, TipoCliente, CondicionEquipo
  exception/              Excepciones y GlobalExceptionHandler
    dto/                  ErrorResponseDTO
  repository/             JpaRepository, consultas y bloqueos
  service/
    generic/              CrudService
    service/              Interfaces y HealthService
    impl/                 Implementaciones transaccionales y mapeo
src/main/resources/       application.yaml, application-dev.yaml, application-prod.yaml
src/test/                 Pruebas aisladas y semilla H2
postman/                  Colección y entorno
docs/                    Guía de defensa, trazabilidad y evidencias pendientes
~~~

## 8. Decisiones de arquitectura

- Los controladores solo validan y delegan. Los servicios reciben DTO y devuelven DTO; no se exponen entidades JPA.
- Las asociaciones ManyToOne son LAZY; open-in-view=false obliga a transformar los DTO dentro de las transacciones de servicio.
- RN-01 a RN-04 están en AlquilerServiceImpl. Un RuntimeException revierte la transacción, incluso si una línea previa ya descontó unidades.
- Alquiler.agregarDetalle establece ambos lados; cascade ALL guarda los detalles y orphanRemoval elimina los retirados por SC-B.
- El detalle copia la tarifa; todos los cálculos usan BigDecimal, escala 2 y HALF_UP. Los cambios futuros de tarifa no alteran los importes ya registrados.
- El cliente se bloquea con PESSIMISTIC_WRITE antes del COUNT de alquileres vigentes. Los equipos se bloquean por ID ascendente para evitar sobreventa; devolución y cambios también bloquean las filas implicadas. Actualizaciones de catálogo usan los mismos bloqueos. Se utiliza la espera normal de bloqueo de la base de datos.
- El nombre de categoría se normaliza sin espacios y en minúsculas en el servicio. nombre_normalizado es una columna adicional permitida por el enunciado y tiene unicidad física como protección concurrente; no hay triggers.
- Las búsquedas usan Specification y una lista cerrada para ordenar. Los reportes usan JPQL GROUP BY y proyección, con rango [primer día, primer día del mes siguiente) sobre fechaRegistro, incluyendo VIGENTE y DEVUELTO. El importe es bruto, antes del descuento.
- Los campos de auditoría heredados se asignan con PrePersist y PreUpdate en las cinco entidades.
- CORS se limita globalmente a http://localhost:4200 para /api/**. SLF4J registra INFO en modificaciones/reportes, WARN en conflictos y ERROR en fallos 500.
- No hay frontend, seguridad, JWT, Pageable, paginación, Spring Data REST, triggers ni procedimientos almacenados.

## 9. Producción

~~~powershell
$env:SPRING_PROFILES_ACTIVE = 'prod'
# Configurar DB_URL, DB_USERNAME y DB_PASSWORD en el entorno.
java -jar target/AlquileresBackend-0.0.1-SNAPSHOT.jar
~~~

prod usa ddl-auto=validate: el esquema debe existir previamente y mantenerse mediante un proceso de migración controlado. No crea tablas. Swagger UI y /v3/api-docs están deshabilitados.

## 10. Entrega y trabajo del equipo

Consultar docs/GUIA_PASO_A_PASO.md, docs/DEFENSA_TECNICA.md, docs/TRAZABILIDAD.md y docs/EVIDENCIAS.md. El historial colaborativo, las revisiones de compañeros, las ramas individuales durante el examen y las capturas Oracle deben proceder de actividad real del equipo. Este proyecto no crea evidencias ficticias de esas actividades.
