package com.damian.escuela.mapper;

import com.damian.escuela.dto.datos.DatosGrupo;
import com.damian.escuela.dto.grupos.GrupoRequest;
import com.damian.escuela.dto.grupos.GrupoResponse;
import com.damian.escuela.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GrupoMapper  implements CommonMapper<GrupoRequest, GrupoResponse, Grupo> {

    private final CursoMapper cursoMapper;
    private final MaestroMapper maestroMapper;
    private final AulaMapper aulaMapper;

    @Override
    public Grupo requestAEntidad(GrupoRequest request) {
        return request == null ? null : Grupo.crear(request.periodo());
    }

    public Grupo requestAEntidad(GrupoRequest request, Curso curso, Maestro maestro, Aula aula) {
        Grupo grupo = requestAEntidad(request);
        grupo.asignarCursoMaestroAula(curso, maestro, aula);

        return grupo;
    }

    @Override
    public GrupoResponse entidadAResponse(Grupo grupo) {
        return grupo == null ? null : new GrupoResponse(
                grupo.getId(),
                cursoMapper.entidadADatosCurso(grupo.getCurso()),
                maestroMapper.entidadADatosMaestro(grupo.getMaestro()),
                aulaMapper.entidadADatosAula(grupo.getAula()),
                grupo.getHorarios().stream().map(Horario::obtenerHorarioCompleto).toList(),
                grupo.getPeriodo()
        );
    }

    public DatosGrupo entidadADatosGrupo(Grupo grupo){
        return grupo == null ? null : new DatosGrupo(
                grupo.getCurso().getNombre(),
                String.join(" ",
                        grupo.getMaestro().getNombre(),
                        grupo.getMaestro().getApellidoPaterno(),
                        grupo.getMaestro().getApellidoMaterno()),
                grupo.getAula().getNombre(),
                grupo.getPeriodo()
        );
    }
}
