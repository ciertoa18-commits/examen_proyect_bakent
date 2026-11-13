package pe.edu.upeu.AlquileresBackend.service.service;

import pe.edu.upeu.AlquileresBackend.dto.HealthResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
@Service @RequiredArgsConstructor @Slf4j
public class HealthService {
    private final DataSource dataSource;
    public HealthResponseDTO comprobar() {
        try(Connection c=dataSource.getConnection(); PreparedStatement st=c.prepareStatement("SELECT 1 FROM DUAL")) {
            st.setQueryTimeout(3); try(ResultSet rs=st.executeQuery()) { if(rs.next()&&rs.getInt(1)==1) return new HealthResponseDTO("UP","UP",LocalDateTime.now()); }
        } catch(SQLException e) { log.warn("Health: base de datos no disponible"); }
        return new HealthResponseDTO("DOWN","DOWN",LocalDateTime.now());
    }
}