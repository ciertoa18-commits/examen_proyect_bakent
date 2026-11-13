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
import pe.edu.upeu.AlquileresBackend.enums.CondicionEquipo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true)
public class EquipoServiceImpl implements EquipoService {
    private final EquipoRepository repository;
    private final CategoriaRepository categorias;
    private final DetalleAlquilerRepository detalles;
    private Equipo obtener(Long id) { return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Equipo no encontrado: "+id)); }
    private void validarCategoria(Long id) { if(!categorias.existsById(id)) throw new RecursoNoEncontradoException("Categoría no encontrada: "+id); }
    private void asignar(Equipo e,EquipoRequestDTO r) {
        Categoria c=categorias.findById(r.categoriaId()).orElseThrow(()->new RecursoNoEncontradoException("Categoría no encontrada: "+r.categoriaId()));
        if(repository.existsByCodigoAndIdNot(r.codigo(),e.getId()==null?0L:e.getId())) throw new ReglaNegocioException("Código de equipo duplicado");
        e.setCodigo(r.codigo()); e.setNombre(r.nombre().strip()); e.setTarifaDiaria(r.tarifaDiaria().setScale(2,RoundingMode.HALF_UP)); e.setUnidadesDisponibles(r.unidadesDisponibles()); e.setEstado(r.estado()); e.setCategoria(c); e.setCondicion(r.condicion());
    }
    @Transactional public EquipoResponseDTO create(EquipoRequestDTO r) { Equipo e=new Equipo(); asignar(e,r); repository.saveAndFlush(e); log.info("Equipo creado: {}",e.getId()); return DtoMapper.equipo(e); }
    public EquipoResponseDTO read(Long id) { return DtoMapper.equipo(obtener(id)); }
    public List<EquipoResponseDTO> readAll() { return repository.findAll(Sort.by("id")).stream().map(DtoMapper::equipo).toList(); }
    @Transactional public EquipoResponseDTO update(Long id,EquipoRequestDTO r) { Equipo e=repository.bloquear(id).orElseThrow(()->new RecursoNoEncontradoException("Equipo no encontrado: "+id)); asignar(e,r); repository.flush(); log.info("Equipo actualizado: {}",id); return DtoMapper.equipo(e); }
    @Transactional public void delete(Long id) { Equipo e=repository.bloquear(id).orElseThrow(()->new RecursoNoEncontradoException("Equipo no encontrado: "+id)); if(detalles.existsByEquipoId(id)) throw new ReglaNegocioException("No se puede eliminar un equipo con detalles de alquiler asociados"); repository.delete(e); log.info("Equipo eliminado: {}",id); }
    public List<EquipoResponseDTO> porCategoria(Long id) { validarCategoria(id); return repository.findByCategoriaIdOrderByNombreAsc(id).stream().map(DtoMapper::equipo).toList(); }
    public List<EquipoResponseDTO> buscar(String nombre,Long categoriaId,BigDecimal tarifaMax,Boolean disponible,String orden,String dir,CondicionEquipo condicion) {
        String campo=switch(orden) { case "nombre"->"nombre"; case "tarifa"->"tarifaDiaria"; case "unidades"->"unidadesDisponibles"; default->throw new IllegalArgumentException("Orden permitido: nombre, tarifa o unidades"); };
        if(!Set.of("asc","desc").contains(dir.toLowerCase(Locale.ROOT))) throw new IllegalArgumentException("Dirección permitida: asc o desc");
        if(tarifaMax!=null&&tarifaMax.signum()<0) throw new IllegalArgumentException("tarifaMax no puede ser negativa");
        if(categoriaId!=null) validarCategoria(categoriaId);
        Specification<Equipo> spec=(r,q,b)->b.conjunction();
        if(nombre!=null&&!nombre.isBlank()) { String patron="%"+nombre.strip().toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%"; spec=spec.and((r,q,b)->b.like(b.lower(r.get("nombre")),patron,'!')); }
        if(categoriaId!=null) spec=spec.and((r,q,b)->b.equal(r.get("categoria").get("id"),categoriaId));
        if(tarifaMax!=null) spec=spec.and((r,q,b)->b.lessThanOrEqualTo(r.get("tarifaDiaria"),tarifaMax));
        if(disponible!=null) spec=spec.and((r,q,b)->disponible?b.greaterThan(r.get("unidadesDisponibles"),0):b.equal(r.get("unidadesDisponibles"),0));
        if(condicion!=null) spec=spec.and((r,q,b)->b.equal(r.get("condicion"),condicion));
        return repository.findAll(spec,Sort.by(Sort.Direction.fromString(dir),campo).and(Sort.by("codigo"))).stream().map(DtoMapper::equipo).toList();
    }
}