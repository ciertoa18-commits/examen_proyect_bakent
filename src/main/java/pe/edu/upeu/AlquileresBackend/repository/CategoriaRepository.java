package pe.edu.upeu.AlquileresBackend.repository;

import pe.edu.upeu.AlquileresBackend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
    boolean existsByNombreNormalizadoAndIdNot(String nombre,Long id);
}