package com.damian.escuela.mapper;

import com.damian.escuela.dto.cursos.CursoRequest;
import com.damian.escuela.dto.cursos.CursoResponse;
import com.damian.escuela.dto.datos.DatosCurso;
import com.damian.escuela.entities.Curso;
import org.springframework.stereotype.Component;

@Component
public class CursoMapper implements CommonMapper<CursoRequest, CursoResponse, Curso> {

    @Override
    public Curso requestAEntidad(CursoRequest request) {
        return request == null ? null : Curso.crear(
                request.nombre(),
                request.descripcion(),
                request.creditos()
        );
    }

    @Override
    public CursoResponse entidadAResponse(Curso entidad) {
        return entidad == null ? null : new CursoResponse(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getCreditos()
        );
    }

    public DatosCurso entidadADatosCurso(Curso entidad){
        return entidad == null ? null : new DatosCurso(
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getCreditos()
        );
    }
}
