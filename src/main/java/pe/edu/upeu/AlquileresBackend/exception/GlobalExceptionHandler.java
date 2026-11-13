package pe.edu.upeu.AlquileresBackend.exception;

import pe.edu.upeu.AlquileresBackend.exception.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import java.time.LocalDateTime;
import java.util.*;
@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponseDTO> error(HttpStatus status, String message, HttpServletRequest r, Map<String,String> fields) {
        return ResponseEntity.status(status).body(new ErrorResponseDTO(LocalDateTime.now(),status.value(),status.getReasonPhrase(),message,r.getRequestURI(),fields));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validacion(MethodArgumentNotValidException e,HttpServletRequest r) {
        Map<String,String> fields=new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f->fields.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST,"Datos de entrada inválidos",r,fields);
    }
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> noExiste(RecursoNoEncontradoException e,HttpServletRequest r) { return error(HttpStatus.NOT_FOUND,e.getMessage(),r,Map.of()); }
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> negocio(ReglaNegocioException e,HttpServletRequest r) { log.warn("Regla de negocio: {}",e.getMessage()); return error(HttpStatus.CONFLICT,e.getMessage(),r,Map.of()); }
    @ExceptionHandler({DataIntegrityViolationException.class, PessimisticLockingFailureException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponseDTO> integridad(Exception e,HttpServletRequest r) { log.warn("Conflicto de integridad o concurrencia: {}",e.getClass().getSimpleName()); return error(HttpStatus.CONFLICT,"Conflicto: valor duplicado, recurso relacionado o modificación concurrente",r,Map.of()); }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDTO> formato(Exception e,HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST,"Petición inválida: revise JSON, parámetros, fechas y valores de enumeraciones",r,Map.of("peticion","Formato o valor inválido")); }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> rutaInexistente(NoResourceFoundException e,HttpServletRequest r) { return error(HttpStatus.NOT_FOUND,"Ruta no encontrada",r,Map.of()); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> metodo(HttpRequestMethodNotSupportedException e,HttpServletRequest r) { return error(HttpStatus.METHOD_NOT_ALLOWED,"Método HTTP no permitido para esta ruta",r,Map.of()); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> inesperado(Exception e,HttpServletRequest r) { log.error("Error inesperado en {}",r.getRequestURI(),e); return error(HttpStatus.INTERNAL_SERVER_ERROR,"Ocurrió un error interno",r,Map.of()); }
}