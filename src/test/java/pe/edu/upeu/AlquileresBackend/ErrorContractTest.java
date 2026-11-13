package pe.edu.upeu.AlquileresBackend;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.AlquileresBackend.controller.HealthController;
import pe.edu.upeu.AlquileresBackend.dto.HealthResponseDTO;
import pe.edu.upeu.AlquileresBackend.exception.GlobalExceptionHandler;
import pe.edu.upeu.AlquileresBackend.service.service.HealthService;
import org.springframework.mock.web.MockHttpServletRequest;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class ErrorContractTest {
    @Test void healthControllerDevuelve503CuandoOracleNoResponde() {
        HealthService service=mock(HealthService.class);
        when(service.comprobar()).thenReturn(new HealthResponseDTO("DOWN","DOWN",LocalDateTime.now()));
        assertThat(new HealthController(service).salud().getStatusCode().value()).isEqualTo(503);
    }
    @Test void error500NoExponeTrazaNiMensajeInterno() {
        var respuesta=new GlobalExceptionHandler().inesperado(new RuntimeException("secreto de prueba"),new MockHttpServletRequest("GET","/api/v1/equipos"));
        assertThat(respuesta.getStatusCode().value()).isEqualTo(500);
        assertThat(respuesta.getBody().message()).isEqualTo("Ocurrió un error interno");
        assertThat(respuesta.getBody().validationErrors()).isEmpty();
    }
}
