package com.damian.escuela.mapper;

import com.damian.escuela.dto.alumnos.AlumnoRequest;
import com.damian.escuela.dto.alumnos.AlumnoResponse;
import com.damian.escuela.dto.datos.DatosAlumno;
import com.damian.escuela.dto.datos.DatosCalificacion;
import com.damian.escuela.entities.Alumno;
import com.damian.escuela.utils.StringCustomUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlumnoMapper implements CommonMapper<AlumnoRequest, AlumnoResponse, Alumno> {

    @Override
    public Alumno requestAEntidad(AlumnoRequest request) {
        return request == null ? null : Alumno.crear(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno()
        );
    }

    public Alumno requestAEntidad(AlumnoRequest request, String email, String matricula) {
        if (request == null) return null;

        Alumno alumno = requestAEntidad(request);
        alumno.asignarDatosAcademicos(email, matricula);
        return alumno;
    }

    @Override
    public AlumnoResponse entidadAResponse(Alumno entidad) {
        return entidad == null ? null : new AlumnoResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEmail(),
                entidad.getMatricula(),
                StringCustomUtils.localDateAString(entidad.getFechaIngreso()),
                entidadADatosCalificacion(entidad),
                entidad.calcularPromedio()
        );
    }

    private List<DatosCalificacion> entidadADatosCalificacion(Alumno alumno){
        if (alumno == null || alumno.getInscripciones() == null || alumno.getInscripciones().isEmpty())
            return List.of();

        return alumno.getInscripciones().stream().map(inscripcion -> new DatosCalificacion(
                inscripcion.getGrupo().getCurso().getNombre(),
                inscripcion.getGrupo().getPeriodo(),
                inscripcion.getCalificacion() != null ? inscripcion.getCalificacion().getCalificacion() : null
        )).toList();
    }

    public DatosAlumno entidadADatosAlumno(Alumno alumno){
        return alumno == null ? null : new DatosAlumno(
                String.join(" ",
                        alumno.getNombre(),
                        alumno.getApellidoPaterno(),
                        alumno.getApellidoMaterno()),
                alumno.getMatricula(),
                alumno.getEmail(),
                StringCustomUtils.localDateAString(alumno.getFechaIngreso())
        );
    }
}
