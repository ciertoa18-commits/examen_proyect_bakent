package pe.edu.upeu.AlquileresBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import pe.edu.upeu.AlquileresBackend.enums.*;
public record CategoriaResponseDTO(Long id, String nombre, String descripcion, Boolean estado) {}