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
@ApiResponses({
    @ApiResponse(responseCode="400", description="Datos inválidos", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class))),
    @ApiResponse(responseCode="404", description="Recurso o referencia inexistente", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class))),
    @ApiResponse(responseCode="409", description="Regla de negocio o asociación", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class))),
    @ApiResponse(responseCode="500", description="Error interno", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class)))
})
@RestController @RequestMapping("/api/v1/alquileres") @RequiredArgsConstructor @Tag(name="alquileres")
public class AlquilerController {
    private final AlquilerService service;
    @ApiResponse(responseCode="201", description="Recurso creado")
    @PostMapping @Operation(summary="Registrar cabecera y detalles de forma atómica (RN-01 a RN-04)")
    public ResponseEntity<AlquilerResponseDTO> crear(@Valid @RequestBody AlquilerRequestDTO r) { var dto=service.create(r); return ResponseEntity.created(java.net.URI.create("/api/v1/alquileres/"+dto.id())).body(dto); }
    @GetMapping @Operation(summary="Listar alquileres con detalles")
    public ResponseEntity<List<AlquilerResponseDTO>> listar() { return ResponseEntity.ok(service.readAll()); }
    @GetMapping("/{id}") @Operation(summary="Consultar alquiler con detalles")
    public ResponseEntity<AlquilerResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.read(id)); }
    @PatchMapping("/{id}/devolver") @Operation(summary="Devolver alquiler VIGENTE y reponer unidades una sola vez")
    public ResponseEntity<AlquilerResponseDTO> devolver(@PathVariable Long id) { return ResponseEntity.ok(service.devolver(id)); }
}