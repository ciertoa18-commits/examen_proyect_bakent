package pe.edu.upeu.AlquileresBackend.service.service;

import pe.edu.upeu.AlquileresBackend.dto.*;
import java.util.List;
public interface AlquilerService {
    AlquilerResponseDTO create(AlquilerRequestDTO request);
    AlquilerResponseDTO read(Long id);
    List<AlquilerResponseDTO> readAll();
    AlquilerResponseDTO devolver(Long id);
}