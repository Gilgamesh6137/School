package com.damian.escuela.utils;

import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
public class ServiceUtils {
    public static <E, ID> E obtenerEntidadException(JpaRepository<E, ID> repository, ID id, Class<E> clase){
        String nombreEntidad = clase.getSimpleName();

        log.info("Buscando {} con ID: {}", nombreEntidad, id);

        return repository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException(nombreEntidad + " no encontrado con ID: " + id));
    }
}
