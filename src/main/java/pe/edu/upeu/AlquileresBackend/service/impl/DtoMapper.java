package pe.edu.upeu.AlquileresBackend.service.impl;

import pe.edu.upeu.AlquileresBackend.entity.*;
import pe.edu.upeu.AlquileresBackend.dto.*;
final class DtoMapper {
    private DtoMapper() {}
    static CategoriaResponseDTO categoria(Categoria c) { return new CategoriaResponseDTO(c.getId(),c.getNombre(),c.getDescripcion(),c.getEstado()); }
    static EquipoResponseDTO equipo(Equipo e) { return new EquipoResponseDTO(e.getId(),e.getCodigo(),e.getNombre(),e.getTarifaDiaria(),e.getUnidadesDisponibles(),e.getEstado(),e.getCategoria().getId(),e.getCategoria().getNombre()); }
    static ClienteResponseDTO cliente(Cliente c) { return new ClienteResponseDTO(c.getId(),c.getCodigo(),c.getDocumento(),c.getNombre(),c.getEmail(),c.getTelefono(),c.getTipo(),c.getEstado()); }
    static AlquilerResponseDTO alquiler(Alquiler a) { return new AlquilerResponseDTO(a.getId(),a.getFechaRegistro(),a.getFechaInicio(),a.getDias(),a.getCliente().getId(),a.getCliente().getNombre(),a.getLugarEvento(),a.getEstado(),a.getFechaDevolucion(),a.getTotalUnidades(),a.getSubtotal(),a.getDescuento(),a.getMontoTotal(),a.getDetalles().stream().map(d->new DetalleAlquilerResponseDTO(d.getId(),d.getEquipo().getId(),d.getEquipo().getCodigo(),d.getEquipo().getNombre(),d.getCantidad(),d.getTarifaDiaria(),d.getImporte())).toList()); }
}