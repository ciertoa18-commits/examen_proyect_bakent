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
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository repository;
    private final EquipoRepository equipos;
    private Categoria obtener(Long id) { return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Categoría no encontrada: "+id)); }
    private void asignar(Categoria c,CategoriaRequestDTO r) {
        String nombre=r.nombre().strip().replaceAll("\\s+"," ");
        String clave=nombre.replaceAll("\\s+","").toLowerCase(Locale.ROOT);
        if (clave.length()<3) throw new IllegalArgumentException("El nombre necesita al menos tres caracteres");
        if (repository.existsByNombreNormalizadoAndIdNot(clave,c.getId()==null?0L:c.getId())) throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        c.setNombre(nombre); c.setNombreNormalizado(clave); c.setDescripcion(r.descripcion()); c.setEstado(r.estado());
    }
    @Transactional public CategoriaResponseDTO create(CategoriaRequestDTO r) { Categoria c=new Categoria(); asignar(c,r); repository.saveAndFlush(c); log.info("Categoría creada: {}",c.getId()); return DtoMapper.categoria(c); }
    public CategoriaResponseDTO read(Long id) { return DtoMapper.categoria(obtener(id)); }
    public List<CategoriaResponseDTO> readAll() { return repository.findAll(org.springframework.data.domain.Sort.by("id")).stream().map(DtoMapper::categoria).toList(); }
    @Transactional public CategoriaResponseDTO update(Long id,CategoriaRequestDTO r) { Categoria c=obtener(id); asignar(c,r); repository.flush(); log.info("Categoría actualizada: {}",id); return DtoMapper.categoria(c); }
    @Transactional public void delete(Long id) { Categoria c=obtener(id); if(equipos.existsByCategoriaId(id)) throw new ReglaNegocioException("No se puede eliminar una categoría con equipos asociados"); repository.delete(c); log.info("Categoría eliminada: {}",id); }
}