package pe.edu.upeu.AlquileresBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import pe.edu.upeu.AlquileresBackend.enums.*;
@Entity @Table(name="categorias") @Getter @Setter
public class Categoria extends Auditable {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=60) private String nombre;
    @Column(length=200) private String descripcion;
    @Column(nullable=false) private Boolean estado = true;
    // Clave canónica adicional: protege la unicidad también ante peticiones concurrentes.
    @Column(name="nombre_normalizado", nullable=false, unique=true, length=60) private String nombreNormalizado;

}