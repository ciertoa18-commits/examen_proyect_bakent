# Evidencias: estado y preparación del PDF final

Este documento es una lista de captura, no una afirmación de que Oracle o Git ya hayan sido demostrados. La implementación y las pruebas automatizadas pueden verificarse sin la contraseña. El PDF final con capturas reales requiere la conexión Oracle y los enlaces/historial del equipo.

## Evidencia ya verificable

- Código en este proyecto y configuración de dev/prod.
- Reportes de pruebas en target/surefire-reports y salida pruebas.log.
- Colección Postman exportada con las peticiones del Anexo B y aserciones.
- Script completo de datos y consultas de verificación.

## Capturas requeridas para cerrar la entrega

1. Portada: integrantes, curso, caso, fecha y URL del repositorio.
2. Arranque con perfil dev y conexión Oracle; ocultar cualquier contraseña.
3. Swagger UI con título Alquileres API y versión v1; mostrar recursos.
4. Health 200/UP y, manteniendo la API abierta, 503/DOWN con Oracle detenido.
5. CP02: 409 por categoría duplicada y CP03: 400 con los cuatro campos.
6. CP04: 404 por categoría ausente y CP05: orden EQP-005, EQP-006.
7. CP06: 201, 18 unidades, subtotal/total 2190.00; consulta de stock 6/14/8.
8. CP07/CP09: cliente inactivo y fecha de ayer, ambos 409.
9. CP08: stock de EQP-007 y conteos de alquileres/detalles antes y después; petición fallida 409. Usar verificacion_oracle.sql.
10. CP10: 1680.00, 252.00 y 1428.00; stock de consola 2.
11. CP11/CP12: segundo vigente aceptado, tercero 409, pantalla mantiene 3.
12. CP13: equipo repetido 409 y stock intacto.
13. CP14: reporte ordenado con cinco resultados, unidades/importes del Anexo B.
14. CP15: DEVUELTO y fecha, segundo intento 409, stock final 10/20/16.
15. CP16: encabezado CORS presente para 4200 y ausente para 3000.
16. Resultado de Maven con todas las pruebas aprobadas.
17. Cambio individual asignado: petición, respuesta y archivos de cada capa que interviene.
18. git log --graph --oneline --all --decorate, git shortlog -sne --all y ramas.
19. Enlaces a por lo menos dos PR cerradas por integrante y comentarios de revisión del compañero.
20. Etiqueta v1.0-unidad1 apuntando al commit efectivamente evaluado y rama sc correspondiente con tres commits reales.

## Montaje del documento

Para cada captura indicar CP/requisito, petición, resultado esperado, resultado observado y una explicación breve. Exportar a PDF cuando todas las evidencias existan. No reemplazar las capturas de Oracle con resultados H2. Adjuntar URL del repositorio, etiqueta y enlaces a las PR. Las partes de defensa y colaboración requieren participación real de los integrantes.
