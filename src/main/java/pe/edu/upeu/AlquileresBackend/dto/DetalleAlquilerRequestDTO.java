package pe.edu.upeu.AlquileresBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import pe.edu.upeu.AlquileresBackend.enums.*;
public record DetalleAlquilerRequestDTO(@NotNull @Positive Long equipoId, @NotNull @Min(1) @Max(1000000) Integer cantidad) {}