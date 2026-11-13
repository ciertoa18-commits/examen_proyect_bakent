package pe.edu.upeu.AlquileresBackend.config;

import java.time.*;
import org.springframework.context.annotation.*;
@Configuration
public class TimeConfig { @Bean public Clock clock() { return Clock.system(ZoneId.of("America/Lima")); } }