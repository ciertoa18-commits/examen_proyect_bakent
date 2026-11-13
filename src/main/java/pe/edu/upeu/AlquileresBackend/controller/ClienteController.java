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
@RestController @RequestMapping("/api/v1/clientes") @RequiredArgsConstructor @Tag(name="clientes")
public class ClienteController {
    private final ClienteService service;
    @ApiResponse(responseCode="201", description="Recurso creado")
    @PostMapping @Operation(summary="Crear cliente")
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteRequestDTO r) { var dto=service.create(r); return ResponseEntity.created(java.net.URI.create("/api/v1/clientes/"+dto.id())).body(dto); }
    @GetMapping @Operation(summary="Listar clientes")
    public ResponseEntity<List<ClienteResponseDTO>> listar() { return ResponseEntity.ok(service.readAll()); }
    @GetMapping("/{id}") @Operation(summary="Consultar por identificador")
    public ResponseEntity<ClienteResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") @Operation(summary="Actualizar cliente")
    public ResponseEntity<ClienteResponseDTO> actualizar(@PathVariable Long id,@Valid @RequestBody ClienteRequestDTO r) { return ResponseEntity.ok(service.update(id,r)); }
    @ApiResponse(responseCode="204", description="Recurso eliminado")
    @DeleteMapping("/{id}") @Operation(summary="Eliminar sin referencias asociadas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }

}
