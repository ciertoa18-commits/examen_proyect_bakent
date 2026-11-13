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
import pe.edu.upeu.AlquileresBackend.enums.EstadoAlquiler;
import java.time.*;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true)
public class AlquilerServiceImpl implements AlquilerService {
    private final AlquilerRepository repository;
    private final ClienteRepository clientes;
    private final EquipoRepository equipos;
    private final Clock clock;
    private Alquiler obtener(Long id) { return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Alquiler no encontrado: "+id)); }
    private Alquiler bloquear(Long id) { return repository.bloquear(id).orElseThrow(()->new RecursoNoEncontradoException("Alquiler no encontrado: "+id)); }
    private Equipo bloquearEquipo(Long id) { return equipos.bloquear(id).orElseThrow(()->new RecursoNoEncontradoException("Equipo no encontrado: "+id)); }
    private void exigirVigente(Alquiler a) { if(a.getEstado()!=EstadoAlquiler.VIGENTE) throw new ReglaNegocioException("RN-02: el alquiler debe estar VIGENTE; no puede devolverse dos veces"); }
    private void recalcular(Alquiler a) {
        int unidades=0; BigDecimal subtotal=BigDecimal.ZERO;
        for(DetalleAlquiler d:a.getDetalles()) { unidades=Math.addExact(unidades,d.getCantidad()); subtotal=subtotal.add(d.getImporte()); }
        subtotal=subtotal.setScale(2,RoundingMode.HALF_UP);
        BigDecimal descuento=(a.getDias()>=7?subtotal.multiply(new BigDecimal("0.15")):BigDecimal.ZERO).setScale(2,RoundingMode.HALF_UP);
        a.setTotalUnidades(unidades); a.setSubtotal(subtotal); a.setDescuento(descuento); a.setMontoTotal(subtotal.subtract(descuento).setScale(2,RoundingMode.HALF_UP));
        if(subtotal.precision()>12) throw new ReglaNegocioException("RN-04: el importe supera el máximo admitido (NUMBER 12,2)");
    }
    @Transactional public AlquilerResponseDTO create(AlquilerRequestDTO r) {
        // RN-01: fechas y cliente activo. El bloqueo del cliente serializa RN-03.
        LocalDate hoy=LocalDate.now(clock);
        if(r.fechaInicio().isBefore(hoy)||r.fechaInicio().isAfter(hoy.plusDays(60))) throw new ReglaNegocioException("RN-01: fechaInicio debe estar entre hoy y los próximos 60 días");
        Cliente cliente=clientes.bloquear(r.clienteId()).orElseThrow(()->new RecursoNoEncontradoException("Cliente no encontrado: "+r.clienteId()));
        if(!Boolean.TRUE.equals(cliente.getEstado())) throw new ReglaNegocioException("RN-01: el cliente está inactivo");
        // RN-03 se obtiene mediante COUNT en la base de datos, sin cargar alquileres.
        if(repository.countByClienteIdAndEstado(cliente.getId(),EstadoAlquiler.VIGENTE)>=2) throw new ReglaNegocioException("RN-03: el cliente ya tiene 2 alquileres VIGENTE");
        Set<Long> ids=new TreeSet<>();
        for(DetalleAlquilerRequestDTO d:r.detalles()) if(!ids.add(d.equipoId())) throw new ReglaNegocioException("RN-04: un alquiler no puede repetir equipos");
        // Orden estable de bloqueo para evitar sobreventa y reducir interbloqueos.
        Map<Long,Equipo> bloqueados=new HashMap<>();
        for(Long id:ids) bloqueados.put(id,bloquearEquipo(id));
        Alquiler a=new Alquiler(); a.setCliente(cliente); a.setFechaRegistro(LocalDateTime.now(clock)); a.setFechaInicio(r.fechaInicio()); a.setDias(r.dias()); a.setLugarEvento(r.lugarEvento()); a.setEstado(EstadoAlquiler.VIGENTE);
        for(DetalleAlquilerRequestDTO linea:r.detalles()) {
            Equipo e=bloqueados.get(linea.equipoId());
            if(!Boolean.TRUE.equals(e.getEstado())) throw new ReglaNegocioException("RN-01: equipo inactivo: "+e.getCodigo());
            if(linea.cantidad()>e.getUnidadesDisponibles()) throw new ReglaNegocioException("RN-02: unidades insuficientes para "+e.getCodigo());
            e.setUnidadesDisponibles(e.getUnidadesDisponibles()-linea.cantidad());
            DetalleAlquiler d=new DetalleAlquiler(); d.setEquipo(e); d.setCantidad(linea.cantidad()); d.setTarifaDiaria(e.getTarifaDiaria().setScale(2,RoundingMode.HALF_UP));
            d.setImporte(d.getTarifaDiaria().multiply(BigDecimal.valueOf(d.getCantidad())).multiply(BigDecimal.valueOf(a.getDias())).setScale(2,RoundingMode.HALF_UP));
            a.agregarDetalle(d);
        }
        recalcular(a); repository.saveAndFlush(a);
        // Los equipos se actualizan por dirty checking dentro de esta misma transacción.
        log.info("Alquiler creado: {}, unidades={}, total={}",a.getId(),a.getTotalUnidades(),a.getMontoTotal());
        return DtoMapper.alquiler(a);
    }
    public AlquilerResponseDTO read(Long id) { return DtoMapper.alquiler(obtener(id)); }
    public List<AlquilerResponseDTO> readAll() { return repository.findAll(org.springframework.data.domain.Sort.by("id")).stream().map(DtoMapper::alquiler).toList(); }
    @Transactional public AlquilerResponseDTO devolver(Long id) {
        Alquiler a=bloquear(id); exigirVigente(a);
        a.getDetalles().stream().sorted(Comparator.comparing(d->d.getEquipo().getId())).forEach(d->{ Equipo e=bloquearEquipo(d.getEquipo().getId()); e.setUnidadesDisponibles(Math.addExact(e.getUnidadesDisponibles(),d.getCantidad())); });
        a.setEstado(EstadoAlquiler.DEVUELTO); a.setFechaDevolucion(LocalDateTime.now(clock)); repository.flush(); log.info("Alquiler devuelto: {}",id); return DtoMapper.alquiler(a);
    }
}