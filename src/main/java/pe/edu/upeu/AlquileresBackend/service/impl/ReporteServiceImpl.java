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
import pe.edu.upeu.AlquileresBackend.dto.reporte.*;
import java.time.*;
import java.time.format.DateTimeParseException;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true)
public class ReporteServiceImpl implements ReporteService {
    private final DetalleAlquilerRepository repository;
    private final CategoriaRepository categorias;
    private YearMonth mes(String periodo) {
        if(periodo==null||!periodo.matches("[0-9]{4}-[0-9]{2}")) throw new IllegalArgumentException("periodo debe tener formato AAAA-MM");
        try { YearMonth m=YearMonth.parse(periodo); if(m.getYear()<1||m.getYear()>9998) throw new IllegalArgumentException("Año inválido"); return m; } catch(DateTimeParseException e) { throw new IllegalArgumentException("Periodo inválido",e); }
    }
    public List<EquipoAlquiladoDTO> equiposAlquilados(String periodo,Long categoriaId) {
        YearMonth m=mes(periodo); if(categoriaId!=null&&!categorias.existsById(categoriaId)) throw new RecursoNoEncontradoException("Categoría no encontrada: "+categoriaId);
        log.info("Reporte equipos alquilados: periodo={}, categoria={}",periodo,categoriaId);
        return repository.equiposAlquilados(m.atDay(1).atStartOfDay(),m.plusMonths(1).atDay(1).atStartOfDay(),categoriaId);
    }
}