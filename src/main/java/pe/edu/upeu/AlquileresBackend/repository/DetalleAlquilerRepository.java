package pe.edu.upeu.AlquileresBackend.repository;

import pe.edu.upeu.AlquileresBackend.entity.DetalleAlquiler;
import pe.edu.upeu.AlquileresBackend.dto.reporte.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
public interface DetalleAlquilerRepository extends JpaRepository<DetalleAlquiler,Long> {
    boolean existsByEquipoId(Long id);
    @Query("""
        select new pe.edu.upeu.AlquileresBackend.dto.reporte.EquipoAlquiladoDTO(e.codigo,e.nombre,sum(d.cantidad),sum(d.importe))
        from DetalleAlquiler d join d.equipo e join d.alquiler a
        where a.fechaRegistro >= :inicio and a.fechaRegistro < :fin
        and (:categoriaId is null or e.categoria.id = :categoriaId)
        group by e.codigo,e.nombre order by sum(d.cantidad) desc,e.codigo asc
        """)
    List<EquipoAlquiladoDTO> equiposAlquilados(@Param("inicio") LocalDateTime inicio,@Param("fin") LocalDateTime fin,@Param("categoriaId") Long categoriaId);
}