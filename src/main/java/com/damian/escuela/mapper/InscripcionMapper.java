package com.damian.escuela.mapper;

import com.damian.escuela.dto.datos.DatosInscripcion;
import com.damian.escuela.dto.inscripciones.InscripcionRequest;
import com.damian.escuela.dto.inscripciones.InscripcionResponse;
import com.damian.escuela.entities.Alumno;
import com.damian.escuela.entities.Grupo;
import com.damian.escuela.entities.Inscripcion;
import com.damian.escuela.utils.StringCustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InscripcionMapper implements CommonMapper<InscripcionRequest, InscripcionResponse, Inscripcion> {

    private final AlumnoMapper alumnoMapper;
    private final GrupoMapper grupoMapper;

    @Override
    public Inscripcion requestAEntidad(InscripcionRequest request) {
        return request == null ? null : Inscripcion.crear();
    }

    public Inscripcion requestAEntidad(InscripcionRequest request, Alumno alumno, Grupo grupo) {
        if (request == null) return null;

        Inscripcion inscripcion = requestAEntidad(request);
        inscripcion.asignarAlumno(alumno);
        inscripcion.asignarGrupo(grupo);
        return inscripcion;
    }

    @Override
    public InscripcionResponse entidadAResponse(Inscripcion inscripcion) {
        return inscripcion == null ? null : new InscripcionResponse(
                inscripcion.getId(),
                alumnoMapper.entidadADatosAlumno(inscripcion.getAlumno()),
                grupoMapper.entidadADatosGrupo(inscripcion.getGrupo()),
                inscripcion.getCalificacion() == null ? null : inscripcion.getCalificacion().getCalificacion(),
                StringCustomUtils.localDateAString(inscripcion.getFechaInscripcion())
        );
    }

    public DatosInscripcion entidadADatosInscripcion(Inscripcion inscripcion){
        return inscripcion == null ? null : new DatosInscripcion(
                alumnoMapper.entidadADatosAlumno(inscripcion.getAlumno()),
                grupoMapper.entidadADatosGrupo(inscripcion.getGrupo()),
                StringCustomUtils.localDateAString(inscripcion.getFechaInscripcion())
        );
    }

    /*
    @Override
    public InscripcionResponse entidadAResponse(Inscripcion entidad) {
        return entidad == null ? null : new InscripcionResponse(
                entidad.getId(),
                entidadADatosAlumno(entidad),
                entidadADatosGrupo(entidad),
                entidad.getCalificacion().getCalificacion(),
                StringCustomUtils.localDateAString(entidad.getFechaInscripcion())
        );
    }

    public DatosInscripcion entidadADatosInscripcion(Inscripcion inscripcion){
        return inscripcion == null ? null
                : new DatosInscripcion(
                alumnoMapper.entidadADatosAlumno(inscripcion.getAlumno()),
                grupoMapper.entidadADatosGrupo(inscripcion.getGrupo()),
                StringCustomUtils.localDateAString(inscripcion.getFechaInscripcion())
        );
    }

    private DatosAlumno entidadADatosAlumno(Inscripcion inscripcion){
        return inscripcion == null ? null : new DatosAlumno(
                String.join(" ",
                        inscripcion.getAlumno().getNombre(),
                        inscripcion.getAlumno().getApellidoPaterno(),
                        inscripcion.getAlumno().getApellidoMaterno()),
                inscripcion.getAlumno().getMatricula(),
                inscripcion.getAlumno().getEmail(),
                StringCustomUtils.localDateAString(inscripcion.getAlumno().getFechaIngreso())
        );
    }

    private DatosGrupo entidadADatosGrupo(Inscripcion inscripcion){
        return inscripcion == null ? null : new DatosGrupo(
                inscripcion.getGrupo().getCurso().getNombre(),
                String.join(" ",
                        inscripcion.getGrupo().getMaestro().getNombre(),
                        inscripcion.getGrupo().getMaestro().getApellidoPaterno(),
                        inscripcion.getGrupo().getMaestro().getApellidoMaterno()),
                inscripcion.getGrupo().getAula().getNombre(),
                inscripcion.getGrupo().getPeriodo()
        );
    }
     */
}
