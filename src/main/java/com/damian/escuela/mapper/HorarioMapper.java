package com.damian.escuela.mapper;

import com.damian.escuela.dto.horarios.HorarioRequest;
import com.damian.escuela.dto.horarios.HorarioResponse;
import com.damian.escuela.entities.Grupo;
import com.damian.escuela.entities.Horario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HorarioMapper implements CommonMapper<HorarioRequest, HorarioResponse, Horario> {

    private final GrupoMapper grupoMapper;
    @Override
    public Horario requestAEntidad(HorarioRequest request) {
        return request == null ? null : Horario.crear(request.dia(), request.horaInicio(), request.horaFin());
    }

    public Horario requestAEntidad(HorarioRequest request, Grupo grupo) {
        Horario horario = requestAEntidad(request);
        horario.asignarGrupo(grupo);

        return horario;
    }

    @Override
    public HorarioResponse entidadAResponse(Horario horario) {
        return horario == null ? null : new HorarioResponse(
                horario.getId(),
                grupoMapper.entidadADatosGrupo(horario.getGrupo()),
                horario.obtenerHorarioCompleto()
        );
    }
}
