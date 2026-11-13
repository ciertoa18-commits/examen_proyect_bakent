package pe.edu.upeu.AlquileresBackend.repository;

import pe.edu.upeu.AlquileresBackend.entity.Alquiler;
import pe.edu.upeu.AlquileresBackend.enums.EstadoAlquiler;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface AlquilerRepository extends JpaRepository<Alquiler,Long> {
    long countByClienteIdAndEstado(Long clienteId,EstadoAlquiler estado);
    boolean existsByClienteId(Long clienteId);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from Alquiler a where a.id = :id")
    Optional<Alquiler> bloquear(@Param("id") Long id);
    @Query("select a from Alquiler a where a.cliente.id = :id and (:estado is null or a.estado = :estado) order by a.fechaInicio desc, a.id desc")
    List<Alquiler> porCliente(@Param("id") Long id,@Param("estado") EstadoAlquiler estado);
}