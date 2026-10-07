package com.damian.escuela.services.inscripciones;

import com.damian.escuela.dto.inscripciones.InscripcionRequest;
import com.damian.escuela.dto.inscripciones.InscripcionResponse;
import com.damian.escuela.entities.Alumno;
import com.damian.escuela.entities.Grupo;
import com.damian.escuela.entities.Inscripcion;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.mapper.InscripcionMapper;
import com.damian.escuela.repositories.AlumnoRepository;
import com.damian.escuela.repositories.CalificacionRepository;
import com.damian.escuela.repositories.GrupoRepository;
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
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final InscripcionMapper inscripcionMapper;
    private final AlumnoRepository alumnoRepository;
    private final GrupoRepository grupoRepository;
    private final CalificacionRepository calificacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> listar() {
        log.info("Listando todas las inscripciones");
        return inscripcionRepository.findAll().stream().map(inscripcionMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse obtenerPorId(Long id) {
        return inscripcionMapper.entidadAResponse(obtenerInscripcion(id));
    }

    @Override
    public InscripcionResponse registrar(InscripcionRequest request) {
        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        validarDatosUnicos(request);
        log.info("Registrando nueva inscripción");

        Inscripcion inscripcion = inscripcionMapper.requestAEntidad(request, alumno, grupo);
        inscripcionRepository.saveAndFlush(inscripcion);

        log.info("Nueva inscripción {} registrada correctamente", inscripcion.getId());
        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    public InscripcionResponse actualizar(InscripcionRequest request, Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);
        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        if (inscripcion.cambioEnDatos(alumno, grupo)) {
            log.info("Actualizando inscripción con ID {}", id);

            validarCambiosUnicos(request, id);
            inscripcion.actualizar(alumno, grupo);
            inscripcionRepository.saveAndFlush(inscripcion);

            log.info("Inscripción con ID {} actualizado correctamente", id);
        }

        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    public void eliminar(Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);
        log.info("Eliminando inscripción con ID {}", id);

        if (calificacionRepository.existsByInscripcionId(inscripcion.getId()))
            throw new EntidadRelacionadaException("No se puede eliminar la inscripción, ya que tiene calificaciones asociadas");

        inscripcionRepository.delete(inscripcion);
        grupoRepository.flush();
        log.info("Inscripción con ID {} eliminada correctamente", id);
    }

    private Inscripcion obtenerInscripcion(Long id){
        return ServiceUtils.obtenerEntidadException(inscripcionRepository, id, Inscripcion.class);
    }

    private Alumno obtenerAlumno(Long id){
        return ServiceUtils.obtenerEntidadException(alumnoRepository, id, Alumno.class);
    }

    private Grupo obtenerGrupo(Long id){
        return ServiceUtils.obtenerEntidadException(grupoRepository, id, Grupo.class);
    }

    private void validarDatosUnicos(InscripcionRequest request) {
        if (inscripcionRepository.existsByAlumnoIdAndGrupoId(request.idAlumno(), request.idGrupo()))
            throw new ConflictoException("El alumno ya está inscrito en este grupo");
    }

    private void validarCambiosUnicos(InscripcionRequest request, Long id) {
        if (inscripcionRepository.existsByAlumnoIdAndGrupoIdAndIdNot(request.idAlumno(), request.idGrupo(), id))
            throw new ConflictoException("El alumno ya está inscrito en este grupo");
    }
}
