-- Consultas de solo lectura para capturar evidencia real en el esquema de alquileres.
SELECT SYS_CONTEXT('USERENV','SESSION_USER') usuario,
       SYS_CONTEXT('USERENV','SERVICE_NAME') servicio FROM dual;
SELECT COUNT(*) categorias FROM categorias;
SELECT COUNT(*) equipos FROM equipos;
SELECT COUNT(*) clientes FROM clientes;
SELECT codigo, unidades_disponibles, estado FROM equipos ORDER BY codigo;
-- Antes y después de CP08: los conteos de cabecera/detalle deben permanecer iguales.
SELECT COUNT(*) cabeceras FROM alquileres;
SELECT COUNT(*) detalles FROM detalle_alquileres;
SELECT codigo, unidades_disponibles FROM equipos WHERE codigo IN ('EQP-007','EQP-004');
-- Después de CP06 y CP10: comprobar importes exactos y tarifa histórica.
SELECT id,estado,total_unidades,subtotal,descuento,monto_total,fecha_registro,fecha_inicio FROM alquileres ORDER BY id;
SELECT alquiler_id,equipo_id,cantidad,tarifa_diaria,importe FROM detalle_alquileres ORDER BY id;
-- Después de CP15: stock repuesto y una sola devolución.
SELECT codigo,unidades_disponibles FROM equipos WHERE codigo IN ('EQP-001','EQP-003','EQP-009') ORDER BY codigo;
SELECT id,estado,fecha_devolucion FROM alquileres ORDER BY id;
-- Nombres, tipos y precisión de las columnas entregadas.
SELECT table_name,column_name,data_type,data_precision,data_scale,nullable FROM user_tab_columns
WHERE table_name IN ('CATEGORIAS','EQUIPOS','CLIENTES','ALQUILERES','DETALLE_ALQUILERES')
ORDER BY table_name,column_id;
