package com.damian.escuela.services.calificacion;

import com.damian.escuela.dto.calificacion.CalificacionRequest;
import com.damian.escuela.dto.calificacion.CalificacionResponse;
import com.damian.escuela.entities.Calificacion;
import com.damian.escuela.entities.Inscripcion;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.mapper.CalificacionMapper;
import com.damian.escuela.repositories.CalificacionRepository;
import com.damian.escuela.repositories.InscripcionRepository;
import com.damian.escuela.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CalificacionServiceImpl implements CalificacionService{

    private final CalificacionRepository calificacionRepository;
    private final CalificacionMapper calificacionMapper;
    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponse> listar() {
        log.info("Listando todas las calificaciones");
        return calificacionRepository.findAll().stream().map(calificacionMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerPorId(Long id) {
        return calificacionMapper.entidadAResponse(obtenerCalificacion(id));
    }

    @Override
    public CalificacionResponse registrar(CalificacionRequest request) {
        Inscripcion inscripcion = obtenerInscripcion(request.idInscripcion());
        log.info("Registrando una nueva calificación");

        if (calificacionRepository.existsByInscripcionId(request.idInscripcion()))
            throw new ConflictoException("La inscripción ya tiene una calificación registrada");

        Calificacion calificacion = calificacionMapper.requestAEntidad(request, inscripcion);
        calificacionRepository.saveAndFlush(calificacion);

        log.info("Nueva calificación {} registrada correctamente", calificacion.getId());
        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    public CalificacionResponse actualizar(CalificacionRequest request, Long id) {
        Calificacion calificacion = obtenerCalificacion(id);
        log.info("Actualizando calificación con ID {}", id);

        calificacion.actualizar(request.calificacion());
        calificacionRepository.saveAndFlush(calificacion);

        log.info("Calificación con ID {} actualizada correctamente", id);
        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    public void eliminar(Long id) {
        Calificacion calificacion = obtenerCalificacion(id);
        log.info("Eliminando calificación con ID {}", id);

        calificacionRepository.delete(calificacion);
        calificacionRepository.flush();
        log.info("Calificación con ID {} eliminada correctamente", id);
    }

    private Calificacion obtenerCalificacion(Long id) {
        return ServiceUtils.obtenerEntidadException(calificacionRepository, id, Calificacion.class);
    }

    private Inscripcion obtenerInscripcion(Long id) {
        return ServiceUtils.obtenerEntidadException(inscripcionRepository, id, Inscripcion.class);
    }
}
