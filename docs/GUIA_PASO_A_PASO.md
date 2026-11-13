# Desarrollo y ejecución paso a paso

## Paso 1. Proyecto independiente

Abrir AlquileresBackend, no la raíz de ClinicaBackend. El nombre, paquete base y aplicación principal son AlquileresBackend. Spring Boot 4.0.7, Java release 21, Maven, JPA, Bean Validation, Web MVC, Oracle y springdoc 3.0.2. H2 y las herramientas de pruebas están limitadas a test. La versión y organización parten del ejemplo que proporcionaste.

## Paso 2. Perfiles

application.yaml define nombre, puerto y open-in-view=false; dev es el perfil predeterminado. application-dev.yaml usa Oracle, variables DB_URL/DB_USERNAME/DB_PASSWORD, ddl-auto update y Swagger activo. application-prod.yaml requiere las tres variables, usa validate y desactiva las dos rutas de documentación. El perfil test vive únicamente en src/test/resources y nunca sustituye Oracle al ejecutar dev.

## Paso 3. Entidades

1. Auditable agrega fecha_creacion y fecha_modificacion mediante callbacks JPA.
2. Categoria mapea categorias, con nombre, descripcion y estado. nombre_normalizado es la clave adicional de unicidad insensible a espacios y mayúsculas.
3. Equipo mapea equipos, con codigo EQP-000, nombre, tarifa_diaria, unidades_disponibles, estado, categoria_id LAZY y condicion de SC-D.
4. Cliente mapea clientes, con codigo CLI-000, documento, nombre, email, telefono, tipo y estado.
5. Alquiler mapea alquileres, con fecha_registro del servidor, fecha_inicio, dias, cliente_id LAZY, lugar_evento, estado, fecha_devolucion y sus totales monetarios.
6. DetalleAlquiler mapea detalle_alquileres. Cada línea guarda equipo_id, cantidad, tarifa_diaria histórica e importe. agregarDetalle establece la relación con la cabecera; cascade ALL y orphanRemoval manejan su ciclo de vida.
7. Los nombres SQL usan guiones bajos sin tildes. Tarifas son NUMBER(10,2); importes y totales, NUMBER(12,2).

## Paso 4. Contrato DTO y validación

Leer los RequestDTO y ResponseDTO de dto/. Los record de entrada llevan NotNull/NotBlank, Size, Pattern, DecimalMin, Digits y Min/Max. El arreglo de detalles lleva validación anidada. Fechas fuera de RN-01 se verifican en servicio para devolver 409, no con Past/Future que produciría 400. Los DTO de respuesta son planos; el alquiler incorpora una lista de DTO de detalle, sin referencias circulares.

## Paso 5. Repositorios

CategoriaRepository verifica unicidad canónica. EquipoRepository permite Specification, consulta por categoría y bloqueo de fila. ClienteRepository verifica código/documento y bloquea el cliente. AlquilerRepository cuenta vigentes con COUNT, bloquea la cabecera y consulta SC-A con orden descendente. DetalleAlquilerRepository resuelve los dos reportes con GROUP BY y proyección JPQL; el reporte SC-C cuenta alquileres distintos, no líneas.

## Paso 6. CRUD por capas

CrudService<Q,R,ID> declara create, readAll, read, update y delete. CategoriaService, EquipoService y ClienteService lo extienden. Sus ServiceImpl validan referencias, unicidad y asociaciones al eliminar. Los controladores reciben DTO, invocan servicio y construyen ResponseEntity con 201, 200 o 204. Revisar cada uno para entender el recorrido.

## Paso 7. Registro transaccional del alquiler

En AlquilerServiceImpl.create:

1. Validar fechaInicio entre hoy y hoy+60 (RN-01).
2. Bloquear el cliente y exigir estado activo (RN-01).
3. Consultar COUNT de alquileres VIGENTE; rechazar si ya son dos (RN-03).
4. Construir un TreeSet de IDs para rechazar equipos repetidos (RN-04).
5. Bloquear equipos por ID ascendente (control de concurrencia).
6. Construir la cabecera con fechaRegistro del servidor y estado VIGENTE.
7. Para cada línea: verificar equipo activo (RN-01), stock suficiente (RN-02), descontar, copiar tarifa y calcular cantidad x tarifa x dias (RN-04).
8. agregarDetalle mantiene ambos lados de la asociación.
9. recalcular suma unidades y subtotal; aplica 15% desde siete días y resta el descuento.
10. saveAndFlush persiste cabecera y detalles. Los cambios en equipos se guardan mediante dirty checking.
11. Cualquier excepción no comprobada revierte todo por Transactional. Los tests no llevan una transacción exterior que pueda ocultar el resultado: consultan la base después de la petición fallida.

## Paso 8. Devolución

Bloquear cabecera, exigir VIGENTE, bloquear los equipos en orden, reponer cantidades, marcar DEVUELTO y fijar fechaDevolucion. Una segunda petición obtiene 409 y no repone de nuevo. No se recalculan precios históricos al devolver.

## Paso 9. Búsqueda y reportes

buscar compone Specification con filtros opcionales. nombre usa contains sin distinguir mayúsculas; tarifaMax es inclusiva; disponible compara stock; condicion cubre SC-D. orden solo admite nombre/tarifa/unidades y dir asc/desc. No hay Pageable.

Los reportes filtran fechaRegistro desde el inicio del mes incluido hasta el inicio del siguiente excluido. No filtran fechaInicio ni estado. Los importes son brutos y el descuento no se resta por línea. El principal ordena unidades descendente y código ascendente. Un mes sin datos produce [].

## Paso 10. Errores, CORS y logs

GlobalExceptionHandler traduce validaciones a 400, ausencias a 404, reglas/integridad a 409 y errores no previstos a 500 genérico. WARN en conflictos, ERROR con traza solo en logs del servidor, INFO en modificaciones y reportes. CorsConfig solo permite http://localhost:4200. HealthService consulta realmente SELECT 1 FROM DUAL y HealthController usa 200 o 503.

## Paso 11. Solicitudes de cambio

- SC-A: ClienteController -> AlquilerService.porCliente -> AlquilerRepository.porCliente. Verifica cliente aunque no tenga alquileres y reutiliza AlquilerResponseDTO.
- SC-B: AlquilerController -> retirarEquipo. Transacción y bloqueo, estado vigente, pertenencia del equipo, impedir vacío, reponer stock, retirarDetalle con orphanRemoval y recalcular descuento/totales.
- SC-C: ReporteController -> ingresosPorCategoria -> JPQL con COUNT DISTINCT, SUM y GROUP BY.
- SC-D: CondicionEquipo en entidad, Request/Response DTO, asignación del servicio, Specification y semilla SQL; se prueba también en Postman.

Están implementadas las cuatro ampliaciones para estudiar el alcance completo. En la evaluación el docente asigna una individual: el trabajo que se realice durante esa parte y sus tres commits debe corresponder a la actividad real del estudiante.

## Paso 12. Ejecutar y demostrar

Seguir los comandos exactos del README: configurar variables, arrancar dev, cargar semilla una vez, comprobar health/Swagger, ejecutar la carpeta Anexo B de Postman y revisar cada aserción. Después ejecutar las ampliaciones, que modifican los reportes. Para capturar el rollback usar docs/verificacion_oracle.sql antes y después de CP08.

## Paso 13. Git colaborativo y entrega

El enunciado exige autoría y revisiones reales. Estos comandos se ejecutan en un repositorio propio acordado con el compañero; reemplazar <apellido> y las URL por valores reales.

~~~powershell
# Crear el repositorio común y una base inicial de configuración.
git init -b main
git add .gitignore pom.xml mvnw mvnw.cmd .mvn
git commit -m "chore: inicializar proyecto Maven de EquipAndes"
git checkout -b develop
git remote add origin <url-del-repositorio>
git push -u origin main
git push -u origin develop

# Antes de cada funcionalidad.
git checkout develop
git pull origin develop
git checkout -b feature/equipos-<apellido>
# Editar y probar únicamente el aporte de esa rama.
git add <archivos-del-aporte>
git commit -m "feat(equipos): agregar catálogo con validación de categoría"
git push -u origin feature/equipos-<apellido>
~~~

1. Repartir tareas y registrarlas como issues. Cada integrante debe aportar varias funcionalidades y al menos dos solicitudes de incorporación.
2. Desarrollar las funcionalidades en ramas desde develop. Mensajes con feat/fix/refactor/test/docs/chore; commits pequeños con cambios reales, distribuidos durante el trabajo.
3. Abrir PR hacia develop y hacer que el compañero lo revise, comente y apruebe; fusionar después. No agregar todos los archivos de este proyecto en un único commit fingiendo un proceso colaborativo.
4. Resolver conflictos en la rama de funcionalidad y repetir las pruebas antes de fusionar.
5. Para la parte individual crear sc-a-<apellido>, sc-b-<apellido>, etc. desde develop; hacer al menos tres commits de trabajo real durante el bloque de 120 minutos y subirlos.
6. Integrar develop a main mediante la revisión acordada. main recibe fusiones del código estable; el único commit inicial anterior es de configuración.
7. Etiquetar el commit realmente evaluado y subir la etiqueta:

~~~powershell
git checkout main
git merge --no-ff develop
git tag -a v1.0-unidad1 -m "Producto Unidad 1 - EquipAndes"
git push origin main --tags
git log --graph --oneline --all --decorate
git shortlog -sne --all
git branch -a
git log --first-parent main --oneline
~~~

8. Incorporar capturas, URL del repositorio, etiqueta y enlaces a las PR revisadas al PDF de evidencias. Consultar EVIDENCIAS.md. Las revisiones, el historial de varias fechas y la defensa individual no se pueden sustituir con archivos generados.
