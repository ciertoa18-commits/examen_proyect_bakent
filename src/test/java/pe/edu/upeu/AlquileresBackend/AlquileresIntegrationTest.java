package pe.edu.upeu.AlquileresBackend;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
@Sql(statements={"DELETE FROM detalle_alquileres","DELETE FROM alquileres","DELETE FROM equipos","DELETE FROM clientes","DELETE FROM categorias","ALTER TABLE categorias ALTER COLUMN id RESTART WITH 1","ALTER TABLE equipos ALTER COLUMN id RESTART WITH 1","ALTER TABLE clientes ALTER COLUMN id RESTART WITH 1","ALTER TABLE alquileres ALTER COLUMN id RESTART WITH 1","ALTER TABLE detalle_alquileres ALTER COLUMN id RESTART WITH 1"},executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts="classpath:semilla-test.sql",executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AlquileresIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private String alquiler(long cliente,int dias,String detalles) {
        return "{\"clienteId\":"+cliente+",\"fechaInicio\":\""+LocalDate.now(ZoneId.of("America/Lima")).plusDays(7)+"\",\"dias\":"+dias+",\"detalles\":["+detalles+"]}";
    }
    private String linea(long equipo,int cantidad) { return "{\"equipoId\":"+equipo+",\"cantidad\":"+cantidad+"}"; }
    private int unidades(long id) { return jdbc.queryForObject("select unidades_disponibles from equipos where id=?",Integer.class,id); }
    @Test void cp06_calculaImportesYDescuentaUnidades() throws Exception {
        mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(1,3,linea(1,4)+","+linea(3,6)+","+linea(9,8))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.totalUnidades").value(18)).andExpect(jsonPath("$.subtotal").value(2190)).andExpect(jsonPath("$.descuento").value(0)).andExpect(jsonPath("$.montoTotal").value(2190));
        assertThat(unidades(1)).isEqualTo(6); assertThat(unidades(3)).isEqualTo(14); assertThat(unidades(9)).isEqualTo(8);
        assertThat(jdbc.queryForObject("select count(*) from detalle_alquileres",Integer.class)).isEqualTo(3);
    }
    @Test void rollbackInclusoSiUnaLineaAnteriorYaDescontoStock() throws Exception {
        mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(2,2,linea(7,2)+","+linea(4,1))))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("RN-02")));
        assertThat(unidades(7)).isEqualTo(8);
        assertThat(jdbc.queryForObject("select count(*) from alquileres",Integer.class)).isZero();
        assertThat(jdbc.queryForObject("select count(*) from detalle_alquileres",Integer.class)).isZero();
    }
    @Test void descuentoSemanalYDevolucionUnaSolaVez() throws Exception {
        mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(2,7,linea(2,2))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.subtotal").value(1680)).andExpect(jsonPath("$.descuento").value(252)).andExpect(jsonPath("$.montoTotal").value(1428));
        assertThat(unidades(2)).isEqualTo(2);
        mvc.perform(patch("/api/v1/alquileres/1/devolver")).andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("DEVUELTO")).andExpect(jsonPath("$.fechaDevolucion").isNotEmpty());
        mvc.perform(patch("/api/v1/alquileres/1/devolver")).andExpect(status().isConflict());
        assertThat(unidades(2)).isEqualTo(4);
    }
    @Test void tercerAlquilerNoModificaStock() throws Exception {
        for(int i=0;i<2;i++)mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(1,1,linea(1,1)))).andExpect(status().isCreated());
        mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(1,1,linea(6,1)))).andExpect(status().isConflict()).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("RN-03")));
        assertThat(unidades(6)).isEqualTo(3);
    }
    @Test void equiposRepetidosYClienteInactivoSon409() throws Exception {
        mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(3,2,linea(7,1)+","+linea(7,2)))).andExpect(status().isConflict());
        mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(4,1,linea(7,1)))).andExpect(status().isConflict());
        assertThat(unidades(7)).isEqualTo(8);
    }
    @Test void fechaAnteriorYDespuesDe60DiasSon409() throws Exception {
        String valida=alquiler(3,1,linea(1,1)); String f=LocalDate.now(ZoneId.of("America/Lima")).plusDays(7).toString();
        for(LocalDate fecha:new LocalDate[]{LocalDate.now(ZoneId.of("America/Lima")).minusDays(1),LocalDate.now(ZoneId.of("America/Lima")).plusDays(61)})
            mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(valida.replace(f,fecha.toString()))).andExpect(status().isConflict());
    }
    @Test void validacionDevuelveLosCuatroCamposYReferencias404() throws Exception {
        String json="""
            {"codigo":"X","nombre":"a","tarifaDiaria":0,"unidadesDisponibles":-1,"estado":true,"categoriaId":1,"condicion":"BUENO"}
            """;
        mvc.perform(post("/api/v1/equipos").contentType("application/json").content(json)).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.codigo").exists()).andExpect(jsonPath("$.validationErrors.nombre").exists()).andExpect(jsonPath("$.validationErrors.tarifaDiaria").exists()).andExpect(jsonPath("$.validationErrors.unidadesDisponibles").exists());
        mvc.perform(post("/api/v1/equipos").contentType("application/json").content(json.replace("\"X\"","\"EQP-099\"").replace("\"a\"","\"Equipo válido\"").replace(":0",":10").replace(":-1",":1").replace("categoriaId\":1","categoriaId\":99"))).andExpect(status().isNotFound());
    }
    @Test void unicidadCategoriaAlCrearYActualizarYAsociaciones() throws Exception {
        for(String nombre:new String[]{" sonido ","So Ni Do"})mvc.perform(post("/api/v1/categorias").contentType("application/json").content("{\"nombre\":\""+nombre+"\",\"estado\":true}")).andExpect(status().isConflict());
        mvc.perform(put("/api/v1/categorias/2").contentType("application/json").content("{\"nombre\":\" SONIDO \",\"estado\":true}")).andExpect(status().isConflict());
        mvc.perform(delete("/api/v1/categorias/1")).andExpect(status().isConflict());
    }
    @Test void busquedaOrdenCondicionYCors() throws Exception {
        mvc.perform(get("/api/v1/equipos/buscar?categoriaId=2&disponible=true&orden=tarifa&dir=asc")).andExpect(status().isOk()).andExpect(jsonPath("$[0].codigo").value("EQP-005")).andExpect(jsonPath("$[1].codigo").value("EQP-006")).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/v1/equipos/buscar?condicion=NUEVO&nombre=PANTALLA")).andExpect(status().isOk()).andExpect(jsonPath("$[0].codigo").value("EQP-006"));
        mvc.perform(get("/api/v1/equipos/buscar?orden=id")).andExpect(status().isBadRequest());
        mvc.perform(options("/api/v1/equipos").header("Origin","http://localhost:4200").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","Content-Type")).andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:4200"));
        mvc.perform(options("/api/v1/equipos").header("Origin","http://localhost:3000").header("Access-Control-Request-Method","POST")).andExpect(status().isForbidden()).andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
    @Test void cambiosAyByCYReporteConDevoluciones() throws Exception {
        mvc.perform(post("/api/v1/alquileres").contentType("application/json").content(alquiler(1,7,linea(1,1)+","+linea(3,2)))).andExpect(status().isCreated());
        mvc.perform(get("/api/v1/clientes/1/alquileres?estado=VIGENTE")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
        mvc.perform(get("/api/v1/clientes/99/alquileres")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/alquileres/1/equipos/3")).andExpect(status().isOk()).andExpect(jsonPath("$.totalUnidades").value(1)).andExpect(jsonPath("$.subtotal").value(560)).andExpect(jsonPath("$.descuento").value(84)).andExpect(jsonPath("$.montoTotal").value(476));
        assertThat(unidades(3)).isEqualTo(20); assertThat(jdbc.queryForObject("select count(*) from detalle_alquileres",Integer.class)).isEqualTo(1);
        mvc.perform(delete("/api/v1/alquileres/1/equipos/1")).andExpect(status().isConflict());
        mvc.perform(patch("/api/v1/alquileres/1/devolver")).andExpect(status().isOk());
        String periodo=YearMonth.now(ZoneId.of("America/Lima")).toString();
        mvc.perform(get("/api/v1/reportes/equipos-alquilados?periodo="+periodo)).andExpect(status().isOk()).andExpect(jsonPath("$[0].codigo").value("EQP-001")).andExpect(jsonPath("$[0].importeBruto").value(560));
        mvc.perform(get("/api/v1/reportes/ingresos-por-categoria?periodo="+periodo)).andExpect(status().isOk()).andExpect(jsonPath("$[0].numeroAlquileres").value(1)).andExpect(jsonPath("$[0].importeBruto").value(560));
        mvc.perform(get("/api/v1/reportes/ingresos-por-categoria?periodo=2000-01")).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/api/v1/reportes/equipos-alquilados?periodo=2026-13")).andExpect(status().isBadRequest());
    }
    @Test void healthConsultaBDYSwaggerExponeContrato() throws Exception {
        mvc.perform(get("/api/v1/health")).andExpect(status().isOk()).andExpect(jsonPath("$.database").value("UP"));
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("$.info.title").value("Alquileres API")).andExpect(jsonPath("$.info.version").value("v1"));
    }
}
