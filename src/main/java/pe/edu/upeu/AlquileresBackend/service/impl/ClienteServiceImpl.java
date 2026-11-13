package pe.edu.upeu.AlquileresBackend.service.impl;

import pe.edu.upeu.AlquileresBackend.dto.*;
import pe.edu.upeu.AlquileresBackend.entity.*;
import pe.edu.upeu.AlquileresBackend.repository.*;
import pe.edu.upeu.AlquileresBackend.exception.*;
import pe.edu.upeu.AlquileresBackend.service.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.math.*;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true)
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository repository;
    private final AlquilerRepository alquileres;
    private Cliente obtener(Long id) { return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Cliente no encontrado: "+id)); }
    private void asignar(Cliente c,ClienteRequestDTO r) {
        Long id=c.getId()==null?0L:c.getId();
        if(repository.existsByCodigoAndIdNot(r.codigo(),id)) throw new ReglaNegocioException("Código de cliente duplicado");
        if(repository.existsByDocumentoAndIdNot(r.documento(),id)) throw new ReglaNegocioException("Documento de cliente duplicado");
        c.setCodigo(r.codigo()); c.setDocumento(r.documento()); c.setNombre(r.nombre().strip()); c.setEmail(r.email().strip()); c.setTelefono(r.telefono().strip()); c.setTipo(r.tipo()); c.setEstado(r.estado());
    }
    @Transactional public ClienteResponseDTO create(ClienteRequestDTO r) { Cliente c=new Cliente(); asignar(c,r); repository.saveAndFlush(c); log.info("Cliente creado: {}",c.getId()); return DtoMapper.cliente(c); }
    public ClienteResponseDTO read(Long id) { return DtoMapper.cliente(obtener(id)); }
    public List<ClienteResponseDTO> readAll() { return repository.findAll(org.springframework.data.domain.Sort.by("id")).stream().map(DtoMapper::cliente).toList(); }
    @Transactional public ClienteResponseDTO update(Long id,ClienteRequestDTO r) { Cliente c=repository.bloquear(id).orElseThrow(()->new RecursoNoEncontradoException("Cliente no encontrado: "+id)); asignar(c,r); repository.flush(); log.info("Cliente actualizado: {}",id); return DtoMapper.cliente(c); }
    @Transactional public void delete(Long id) { Cliente c=repository.bloquear(id).orElseThrow(()->new RecursoNoEncontradoException("Cliente no encontrado: "+id)); if(alquileres.existsByClienteId(id)) throw new ReglaNegocioException("No se puede eliminar un cliente con alquileres asociados"); repository.delete(c); log.info("Cliente eliminado: {}",id); }
}