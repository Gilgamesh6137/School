package com.damian.escuela.mapper;

import com.damian.escuela.dto.datos.DatosCurso;
import com.damian.escuela.dto.datos.DatosMaestro;
import com.damian.escuela.dto.maestros.MaestroRequest;
import com.damian.escuela.dto.maestros.MaestroResponse;
import com.damian.escuela.entities.Grupo;
import com.damian.escuela.entities.Maestro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaestroMapper implements CommonMapper<MaestroRequest, MaestroResponse, Maestro> {

    private final CursoMapper cursoMapper;

    @Override
    public Maestro requestAEntidad(MaestroRequest request) {
        return request == null ? null : Maestro.crear(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email(),
                request.telefono()
        );
    }

    @Override
    public MaestroResponse entidadAResponse(Maestro entidad) {
        return entidad == null ? null : new MaestroResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidadADatosCurso(entidad)
        );
    }

    private List<DatosCurso> entidadADatosCurso(Maestro entidad){
        return entidad == null ? List.of() : entidad.getGrupos()
                .stream()
                .map(Grupo::getCurso)
                .map(cursoMapper::entidadADatosCurso)
                .toList();
    }

    public DatosMaestro entidadADatosMaestro(Maestro maestro){
        return maestro == null ? null : new DatosMaestro(
                String.join(" ",
                        maestro.getNombre(),
                        maestro.getApellidoPaterno(),
                        maestro.getApellidoMaterno()),
                maestro.getEmail(),
                maestro.getTelefono()
        );
    }
}
