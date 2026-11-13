package pe.edu.upeu.AlquileresBackend.service.service;

import pe.edu.upeu.AlquileresBackend.dto.reporte.*;
import java.util.List;
public interface ReporteService {
    List<EquipoAlquiladoDTO> equiposAlquilados(String periodo,Long categoriaId);
    List<IngresoCategoriaDTO> ingresosPorCategoria(String periodo);
}