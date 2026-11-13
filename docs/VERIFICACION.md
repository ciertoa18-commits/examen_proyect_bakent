# Verificacion realizada

- Fecha: 13 de noviembre de 2026.
- Compilacion configurada para Java 21 (release 21). JDK instalado usado para verificar: Java 24.
- Maven 3.9.11, Spring Boot 4.0.7.
- Pruebas: 14; fallos: 0; errores: 0; omitidas: 0.
- Integracion MockMvc/JPA/H2: 11 pruebas.
- Contrato de errores/health: 3 pruebas.
- Las pruebas de rollback consultan stock y conteos de tablas despues de la peticion fallida.
- Coleccion Postman: 40 peticiones; formato JSON y sintaxis JavaScript validados. Ejecucion contra Oracle pendiente.
- JAR: target/AlquileresBackend-0.0.1-SNAPSHOT.jar.
- Oracle dev: pendiente de credenciales y comprobacion real.
- PDF con capturas Oracle y evidencias colaborativas Git: pendiente de la ejecucion y actividad reales.

La prueba de 500 provoca deliberadamente una excepcion y comprueba que el cliente recibe un mensaje generico; el ERROR en el log de esa prueba es esperado. El resultado definitivo de la ejecucion esta en pruebas.log y target/surefire-reports.
