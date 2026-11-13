package pe.edu.upeu.AlquileresBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
@MappedSuperclass @Getter
public abstract class Auditable {
    @Column(name="fecha_creacion", nullable=false, updatable=false) private LocalDateTime fechaCreacion;
    @Column(name="fecha_modificacion") private LocalDateTime fechaModificacion;
    @PrePersist protected void crearFecha() { fechaCreacion = LocalDateTime.now(); }
    @PreUpdate protected void actualizarFecha() { fechaModificacion = LocalDateTime.now(); }
}