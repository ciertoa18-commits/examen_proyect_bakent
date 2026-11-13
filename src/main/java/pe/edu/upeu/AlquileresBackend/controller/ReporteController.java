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
import pe.edu.upeu.AlquileresBackend.dto.reporte.*;
@ApiResponses({
    @ApiResponse(responseCode="400", description="Datos inválidos", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class))),
    @ApiResponse(responseCode="404", description="Recurso o referencia inexistente", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class))),
    @ApiResponse(responseCode="409", description="Regla de negocio o asociación", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class))),
    @ApiResponse(responseCode="500", description="Error interno", content=@Content(schema=@Schema(implementation=ErrorResponseDTO.class)))
})
@RestController @RequestMapping("/api/v1/reportes") @RequiredArgsConstructor @Tag(name="reportes")
public class ReporteController {
    private final ReporteService service;
    @GetMapping("/equipos-alquilados") @Operation(summary="Unidades e importe bruto por equipo según mes de registro (AAAA-MM)")
    public ResponseEntity<List<EquipoAlquiladoDTO>> equipos(@RequestParam String periodo,@RequestParam(required=false) Long categoriaId) { return ResponseEntity.ok(service.equiposAlquilados(periodo,categoriaId)); }
    @GetMapping("/ingresos-por-categoria") @Operation(summary="SC-C: alquileres distintos, unidades e importe bruto por categoría")
    public ResponseEntity<List<IngresoCategoriaDTO>> categorias(@RequestParam String periodo) { return ResponseEntity.ok(service.ingresosPorCategoria(periodo)); }
}