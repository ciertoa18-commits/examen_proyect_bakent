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
@RestController @RequestMapping("/api/v1/categorias") @RequiredArgsConstructor @Tag(name="categorias")
public class CategoriaController {
    private final CategoriaService service;
    @ApiResponse(responseCode="201", description="Recurso creado")
    @PostMapping @Operation(summary="Crear categoria")
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO r) { var dto=service.create(r); return ResponseEntity.created(java.net.URI.create("/api/v1/categorias/"+dto.id())).body(dto); }
    @GetMapping @Operation(summary="Listar categorias")
    public ResponseEntity<List<CategoriaResponseDTO>> listar() { return ResponseEntity.ok(service.readAll()); }
    @GetMapping("/{id}") @Operation(summary="Consultar por identificador")
    public ResponseEntity<CategoriaResponseDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") @Operation(summary="Actualizar categoria")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id,@Valid @RequestBody CategoriaRequestDTO r) { return ResponseEntity.ok(service.update(id,r)); }
    @ApiResponse(responseCode="204", description="Recurso eliminado")
    @DeleteMapping("/{id}") @Operation(summary="Eliminar sin referencias asociadas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }

    private final EquipoService equipos;
    @GetMapping("/{id}/equipos") @Operation(summary="Listar equipos de una categoría")
    public ResponseEntity<List<EquipoResponseDTO>> equipos(@PathVariable Long id) { return ResponseEntity.ok(equipos.porCategoria(id)); }
}
