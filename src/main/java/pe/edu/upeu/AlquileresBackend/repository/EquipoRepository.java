package pe.edu.upeu.AlquileresBackend.repository;

import pe.edu.upeu.AlquileresBackend.entity.Equipo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface EquipoRepository extends JpaRepository<Equipo,Long>, JpaSpecificationExecutor<Equipo> {
    boolean existsByCodigoAndIdNot(String codigo,Long id);
    boolean existsByCategoriaId(Long id);
    List<Equipo> findByCategoriaIdOrderByNombreAsc(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select e from Equipo e where e.id = :id")
    Optional<Equipo> bloquear(@Param("id") Long id);
}