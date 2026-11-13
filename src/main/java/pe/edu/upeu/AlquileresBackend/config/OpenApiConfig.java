package pe.edu.upeu.AlquileresBackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;
@Configuration
public class OpenApiConfig {
    @Bean public OpenAPI alquileresOpenApi() { return new OpenAPI().info(new Info().title("Alquileres API").version("v1").description("EquipAndes: alquiler de equipos para eventos. RF-01 a RF-07 y SC-A a SC-D.")); }
}