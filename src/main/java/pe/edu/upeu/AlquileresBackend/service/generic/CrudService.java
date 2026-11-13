package pe.edu.upeu.AlquileresBackend.service.generic;

import java.util.List;
public interface CrudService<Q,R,ID> {
    R create(Q request); List<R> readAll(); R read(ID id); R update(ID id,Q request); void delete(ID id);
}