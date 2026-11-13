package pe.edu.upeu.AlquileresBackend.controller;

import pe.edu.upeu.AlquileresBackend.dto.*;
import pe.edu.upeu.AlquileresBackend.service.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import pe.edu.upeu.AlquileresBackend.exception.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/health") @RequiredArgsConstructor @Tag(name="salud")
public class HealthController {
    private final HealthService service;
    @ApiResponse(responseCode="200", description="Conexión disponible")
    @ApiResponse(responseCode="503", description="Base de datos no disponible", content=@Content(schema=@Schema(implementation=HealthResponseDTO.class)))
    @GetMapping @Operation(summary="Comprobar conexión real con SELECT 1 FROM DUAL; 503 si falla")
    public ResponseEntity<HealthResponseDTO> salud() { var dto=service.comprobar(); return ResponseEntity.status("UP".equals(dto.database())?200:503).body(dto); }
}