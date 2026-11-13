package pe.edu.upeu.AlquileresBackend.service.service;

import pe.edu.upeu.AlquileresBackend.dto.*;
import pe.edu.upeu.AlquileresBackend.service.generic.CrudService;
import java.util.List;
import java.math.BigDecimal;

public interface EquipoService extends CrudService<EquipoRequestDTO,EquipoResponseDTO,Long> {
    List<EquipoResponseDTO> porCategoria(Long id);
    List<EquipoResponseDTO> buscar(String nombre,Long categoriaId,BigDecimal tarifaMax,Boolean disponible,String orden,String dir);
}