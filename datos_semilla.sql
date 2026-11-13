-- Oracle 21c/23ai. Ejecutar UNA VEZ en un esquema vacío, después del primer arranque dev.
-- No borra ni reemplaza datos. El script falla si los códigos ya existen.
-- SQL Developer: Ejecutar script (F5). SQLcl/SQL*Plus: @datos_semilla.sql
SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;
INSERT INTO categorias (nombre,nombre_normalizado,descripcion,estado,fecha_creacion) VALUES ('Sonido','sonido','Parlantes, consolas y micrófonos',1,CURRENT_TIMESTAMP);
INSERT INTO categorias (nombre,nombre_normalizado,descripcion,estado,fecha_creacion) VALUES ('Proyección','proyección','Proyectores, ecran y pantallas',1,CURRENT_TIMESTAMP);
INSERT INTO categorias (nombre,nombre_normalizado,descripcion,estado,fecha_creacion) VALUES ('Iluminación','iluminación','Luces y efectos',1,CURRENT_TIMESTAMP);
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-001','Parlante activo de 15 pulgadas',80.00,10,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Sonido';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-002','Consola de mezcla de 12 canales',120.00,4,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Sonido';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-003','Micrófono inalámbrico',35.00,20,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Sonido';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-004','Proyector de 5000 lúmenes',150.00,0,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Proyección';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-005','Ecran trípode de 100 pulgadas',40.00,6,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Proyección';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-006','Pantalla LED de 55 pulgadas',110.00,3,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Proyección';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-007','Cabeza móvil LED',90.00,8,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Iluminación';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-008','Máquina de humo',50.00,0,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Iluminación';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-009','Reflector PAR LED RGB',25.00,16,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Iluminación';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-010','Subwoofer activo',100.00,5,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Sonido';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-011','Micrófono de solapa',75.00,4,0,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Sonido';
INSERT INTO equipos (codigo,nombre,tarifa_diaria,unidades_disponibles,estado,categoria_id,fecha_creacion) SELECT 'EQP-012','Barra LED',45.00,12,1,id,CURRENT_TIMESTAMP FROM categorias WHERE nombre='Iluminación';
INSERT INTO clientes (codigo,documento,nombre,email,telefono,tipo,estado,fecha_creacion) VALUES ('CLI-001','20601112223','Eventos Misti SAC','contacto@eventosmisti.pe','959000000','EMPRESA',1,CURRENT_TIMESTAMP);
INSERT INTO clientes (codigo,documento,nombre,email,telefono,tipo,estado,fecha_creacion) VALUES ('CLI-002','20455566677','Hotel Plaza Sur','eventos@plazasur.pe','959000000','EMPRESA',1,CURRENT_TIMESTAMP);
INSERT INTO clientes (codigo,documento,nombre,email,telefono,tipo,estado,fecha_creacion) VALUES ('CLI-003','42315678','Carlos Huanca Rojas','carlos.huanca@gmail.com','959000000','PERSONA',1,CURRENT_TIMESTAMP);
INSERT INTO clientes (codigo,documento,nombre,email,telefono,tipo,estado,fecha_creacion) VALUES ('CLI-004','20509988776','Producciones Sur EIRL','ventas@prodsur.pe','959000000','EMPRESA',0,CURRENT_TIMESTAMP);
INSERT INTO clientes (codigo,documento,nombre,email,telefono,tipo,estado,fecha_creacion) VALUES ('CLI-005','45678901','María Quispe Flores','maria@example.com','959000000','PERSONA',1,CURRENT_TIMESTAMP);
INSERT INTO clientes (codigo,documento,nombre,email,telefono,tipo,estado,fecha_creacion) VALUES ('CLI-006','20612345678','Eventos Andinos SAC','contacto@example.com','959000000','EMPRESA',1,CURRENT_TIMESTAMP);
COMMIT;
