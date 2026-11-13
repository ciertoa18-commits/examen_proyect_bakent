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
@RestController @RequestMapping("/api/v1/equipos") @RequiredArgsConstructor @Tag(name="equipos")
public class EquipoController {
    private final EquipoService service;
    @ApiResponse(responseCode="201", description="Recurso creado")
    @PostMapping @Operation(summary="Crear equipo")
    public ResponseEntity<EquipoResponseDTO> crear(@Valid @RequestBody EquipoRequestDTO r) { var dto=service.create(r); return ResponseEntity.created(java.net.URI.create("/api/v1/equipos/"+dto.id())).body(dto); }
    @GetMapping @Operation(summary="Listar equipos")
    public ResponseEntity<List<EquipoResponseDTO>> listar() { return ResponseEntity.ok(service.readAll()); }
    @GetMapping("/{id}") @Operation(summary="Consultar por identificador")
    public ResponseEntity<EquipoResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") @Operation(summary="Actualizar equipo")
    public ResponseEntity<EquipoResponseDTO> actualizar(@PathVariable Long id,@Valid @RequestBody EquipoRequestDTO r) { return ResponseEntity.ok(service.update(id,r)); }
    @ApiResponse(responseCode="204", description="Recurso eliminado")
    @DeleteMapping("/{id}") @Operation(summary="Eliminar sin referencias asociadas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }

    @GetMapping("/buscar") @Operation(summary="Buscar con filtros combinables y orden validado; incluye condición SC-D")
    public ResponseEntity<List<EquipoResponseDTO>> buscar(@RequestParam(required=false) String nombre,@RequestParam(required=false) Long categoriaId,@RequestParam(required=false) java.math.BigDecimal tarifaMax,@RequestParam(required=false) Boolean disponible,@RequestParam(defaultValue="nombre") String orden,@RequestParam(defaultValue="asc") String dir,@RequestParam(required=false) pe.edu.upeu.AlquileresBackend.enums.CondicionEquipo condicion) { return ResponseEntity.ok(service.buscar(nombre,categoriaId,tarifaMax,disponible,orden,dir,condicion)); }
}
