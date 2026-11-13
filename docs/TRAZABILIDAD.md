# Trazabilidad del enunciado

| Requisito | Archivos principales | Comprobación |
| --- | --- | --- |
| RF-01 | HealthController, HealthService, OpenApiConfig | healthConsultaBDYSwaggerExponeContrato, HealthServiceTest, ErrorContractTest, CP01; Oracle 503 manual pendiente |
| RF-02 | CategoriaController/ServiceImpl/Repository | unicidadCategoriaAlCrearYActualizarYAsociaciones, CP02 |
| RF-03 | EquipoController/ServiceImpl/Repository, CategoriaController | validacionDevuelveLosCuatroCamposYReferencias404; GET categoría/equipos |
| RF-04 | ClienteController/ServiceImpl/Repository | Bean Validation, unicidad de código/documento, DTO y CRUD |
| RF-05 | AlquilerController/ServiceImpl, Alquiler y DetalleAlquiler | CP06 a CP13 y CP15; pruebas de importes, rollback y devolución |
| RF-06 | EquipoServiceImpl.buscar | Specification, orden cerrado, busquedaOrdenCondicionYCors y CP05 |
| RF-07 | ReporteController/ServiceImpl, DetalleAlquilerRepository, CorsConfig | GROUP BY/proyección, CP14 y CP16 |
| RN-01 | AlquilerServiceImpl.create | fechas 0..60 días, cliente/equipo activos, CP07/CP09 |
| RN-02 | create/devolver | bloqueo, descuento y reposición, rollback CP08, doble devolución CP15 |
| RN-03 | create y countByClienteIdAndEstado | tercerAlquilerNoModificaStock, CP12 |
| RN-04 | create/recalcular | duplicados, copia de tarifa y 15%, CP06/CP10/CP13 |
| SC-A | ClienteController, AlquilerServiceImpl.porCliente, AlquilerRepository | filtro opcional, orden de inicio descendente y 404 |
| SC-B | AlquilerController, retirarEquipo, retirarDetalle | orphanRemoval, reposición y totales, no vacío |
| SC-C | ReporteController e ingresosPorCategoria | COUNT DISTINCT y GROUP BY, mes sin datos [] |
| SC-D | CondicionEquipo, Equipo y DTO, buscar y semilla | validación, asignación, respuesta y filtro |
| Auditoría | Auditable | PrePersist/PreUpdate heredados en las cinco entidades |
| Perfiles | application*.yaml | dev Oracle/update/Swagger; prod variables/validate/sin Swagger |
| Errores | GlobalExceptionHandler y ErrorResponseDTO | 400/404/409/500; 500 no filtra detalles |
| Concurrencia | bloquear de los repositorios | cliente antes del COUNT; equipos ordenados, escrituras transaccionales |
| Semilla | datos_semilla.sql | 3 categorías, 12 equipos, 6 clientes; stock cero y un equipo/cliente inactivo |
| Colección | postman/EquipAndes.postman_collection.json | CP01-16, verificaciones auxiliares y SC-A a SC-D |
| Git colaborativo | Actividad real del equipo | pendiente ramas, commits distribuidos, revisiones, PR y etiqueta reales |
| Evidencias PDF | Capturas de la ejecución real | pendiente conexión Oracle y colaboración del equipo |

Los reportes de Maven están en target/surefire-reports después de ejecutar test; pruebas.log contiene la salida de la verificación realizada en este equipo. Una prueba H2 aprobada no acredita una conexión Oracle.
