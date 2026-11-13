package pe.edu.upeu.AlquileresBackend.service.service;

import pe.edu.upeu.AlquileresBackend.dto.*;
import pe.edu.upeu.AlquileresBackend.enums.EstadoAlquiler;
import java.util.List;
public interface AlquilerService {
    AlquilerResponseDTO create(AlquilerRequestDTO request);
    AlquilerResponseDTO read(Long id);
    List<AlquilerResponseDTO> readAll();
    AlquilerResponseDTO devolver(Long id);
    List<AlquilerResponseDTO> porCliente(Long id,EstadoAlquiler estado);
    AlquilerResponseDTO retirarEquipo(Long id,Long equipoId);
}