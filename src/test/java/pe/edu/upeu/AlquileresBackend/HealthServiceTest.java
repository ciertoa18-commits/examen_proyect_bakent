package pe.edu.upeu.AlquileresBackend;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.AlquileresBackend.service.service.HealthService;
import javax.sql.DataSource;
import java.sql.SQLException;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class HealthServiceTest {
    @Test void conexionFallidaInformaDown() throws Exception {
        DataSource ds=mock(DataSource.class); when(ds.getConnection()).thenThrow(new SQLException("Detalle privado del servidor"));
        var resultado=new HealthService(ds).comprobar();
        assertThat(resultado.status()).isEqualTo("DOWN"); assertThat(resultado.database()).isEqualTo("DOWN"); assertThat(resultado.timestamp()).isNotNull();
    }
}
