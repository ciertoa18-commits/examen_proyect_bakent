package pe.edu.upeu.AlquileresBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import pe.edu.upeu.AlquileresBackend.enums.*;
public record ClienteRequestDTO(@NotBlank @Pattern(regexp="^CLI-\\d{3}$") String codigo, @NotBlank @Pattern(regexp="^(\\d{8}|\\d{11})$") String documento,
    @NotBlank @Size(max=150) String nombre, @NotBlank @Email @Size(max=150) String email,
    @NotBlank @Size(max=20) String telefono, @NotNull TipoCliente tipo, @NotNull Boolean estado) {}