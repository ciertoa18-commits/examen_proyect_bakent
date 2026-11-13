# Defensa técnica: guía sobre este código

Usar estas respuestas como recorrido de estudio y comprobar cada explicación abriendo los archivos. La defensa exige comprender cada línea.

1. **RN-03:** AlquilerServiceImpl.create invoca countByClienteIdAndEstado después de bloquear el cliente. Con dos vigentes lanza ReglaNegocioException. El repositorio ejecuta COUNT y el servicio toma la decisión. El controlador solo gestiona HTTP; otras entradas pueden reutilizar el mismo servicio.
2. **Cambiar a PostgreSQL:** modificar pom.xml para reemplazar ojdbc11 por el driver PostgreSQL; cambiar driver y URL en application-dev.yaml y application-prod.yaml; adaptar datos_semilla.sql y verificacion_oracle.sql a PostgreSQL; cambiar SELECT 1 FROM DUAL por SELECT 1 en HealthService; preparar y migrar el esquema con tipos y generación de IDs compatibles. Las entidades, DTO, consultas JPQL y servicios deberían conservarse, verificándolo con pruebas de integración contra PostgreSQL.
3. **Tarifa copiada:** create asigna d.setTarifaDiaria con la tarifa del equipo en ese instante y calcula d.importe. Actualizar la tarifa del equipo después no altera la línea ni el reporte histórico. Conservar ese valor evita recalcular contratos anteriores a tarifas nuevas.
4. **Recorrido POST:** JSON -> AlquilerRequestDTO -> Bean Validation y validación anidada -> AlquilerController.crear -> proxy transaccional de AlquilerServiceImpl.create -> repositorios y bloqueos -> cabecera/detalles y descuento -> saveAndFlush/cascade -> dirty checking de equipos -> commit -> DTO y ResponseEntity 201. Si hay excepción, rollback y GlobalExceptionHandler.
5. **DTO frente a entidad:** Equipo contiene auditoría y una asociación Categoria LAZY. EquipoResponseDTO contiene los datos públicos y categoriaId/categoriaNombre ya resueltos. El DTO estabiliza el contrato y evita serializar proxies, auditoría o relaciones circulares.
6. **Rollback:** ejecutar CP08 con EQP-007 primero y EQP-004 después. El servicio descuenta al primero y falla por stock del segundo; RuntimeException hace rollback. Mostrar que EQP-007 sigue en 8 y que no hay nueva cabecera ni detalles. Consultas en verificacion_oracle.sql y prueba rollbackInclusoSiUnaLineaAnteriorYaDescontoStock.
7. **Sin save de equipos:** las filas se cargan como entidades gestionadas dentro de Transactional. Hibernate detecta sus cambios y emite UPDATE al flush/commit. Fuera de una transacción y con entidades desligadas no se puede asumir ese comportamiento.
8. **Importes:** cantidad x tarifa histórica x días. subtotal es SUM de importes y descuento es 15% si dias >= 7, redondeado HALF_UP a dos decimales. BigDecimal evita errores binarios de double. Ejemplo: 2 x 120 x 7 = 1680; descuento 252; total 1428.
9. **409:** el servicio lanza ReglaNegocioException con RN y explicación. El advice la captura, registra WARN y devuelve ErrorResponseDTO con status 409. Al ser RuntimeException también revierte los cambios de la transacción.
10. **CORS:** el backend devuelve los permisos que el navegador verifica. OPTIONS con Origin 4200 y Access-Control-Request-Method POST debe responder el encabezado permitido; 3000 recibe rechazo y no obtiene Access-Control-Allow-Origin. Está en CorsConfig, test busquedaOrdenCondicionYCors y CP16 de Postman.
11. **Concurrencia:** PESSIMISTIC_WRITE mantiene el bloqueo hasta commit/rollback. Dos clientes que intentan tomar las últimas unidades del mismo equipo se serializan; el segundo ve stock actualizado y puede recibir 409. Los equipos se bloquean en orden de ID. El bloqueo del cliente evita que dos solicitudes simultáneas pasen juntas el COUNT de RN-03. Los límites de espera y recuperación dependen de Oracle; los conflictos de bloqueo se traducen a 409.
12. **Aporte Git:** esta respuesta debe corresponder a tu trabajo real. Mostrar tu rama, commits y PR revisadas, explicar una decisión y el conflicto concreto resuelto. No atribuir a un compañero ni al historial de clase los archivos creados aquí. Consultar GUIA_PASO_A_PASO.md para el flujo exigido.

## Demostraciones adicionales útiles

- Crear un alquiler, actualizar la tarifa del equipo y consultar el alquiler: su tarifa e importe permanecen iguales.
- Devolver, repetir y comprobar que el stock solo subió una vez.
- Intentar eliminar una categoría con equipos: 409; consultar una referencia desconocida: 404.
- Retirar una línea de un alquiler semanal: cambian subtotal, descuento y total, se repone stock y la línea desaparece físicamente.
- Mostrar GROUP BY y COUNT DISTINCT de ingresosPorCategoria, y el rango mensual por fechaRegistro.
