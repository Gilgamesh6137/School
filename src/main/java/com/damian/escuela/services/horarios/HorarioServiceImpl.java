package com.damian.escuela.services.horarios;

import com.damian.escuela.dto.horarios.HorarioRequest;
import com.damian.escuela.dto.horarios.HorarioResponse;
import com.damian.escuela.entities.Grupo;
import com.damian.escuela.entities.Horario;
import com.damian.escuela.enums.DiaSemama;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.mapper.HorarioMapper;
import com.damian.escuela.repositories.GrupoRepository;
import com.damian.escuela.repositories.HorarioRepository;
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
public class HorarioServiceImpl implements HorarioService{

    private final HorarioRepository horarioRepository;
    private final HorarioMapper horarioMapper;
    private final GrupoRepository grupoRepository;

    @Override
    public List<HorarioResponse> listar() {
        log.info("Listando todos los horarios");
        return horarioRepository.findAll().stream().map(horarioMapper::entidadAResponse).toList();
    }

    @Override
    public HorarioResponse obtenerPorId(Long id) {
        return horarioMapper.entidadAResponse(obtenerHorario(id));
    }

    @Override
    public HorarioResponse registrar(HorarioRequest request) {
        Grupo grupo = obtenerGrupo(request.idGrupo());
        DiaSemama dia = DiaSemama.obtenerDiaSemanaPorDescripcion(request.dia());

        validarHorario(grupo, dia, request.horaInicio(), request.horaFin(), -1L);
        log.info("Registrando un nuevo horario");

        Horario horario = horarioMapper.requestAEntidad(request, grupo);
        horarioRepository.saveAndFlush(horario);

        log.info("Nuevo horario {} registrado correctamente", horario.getId());
        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public HorarioResponse actualizar(HorarioRequest request, Long id) {
        Horario horario = obtenerHorario(id);
        Grupo grupo = obtenerGrupo(request.idGrupo());
        DiaSemama dia = DiaSemama.obtenerDiaSemanaPorDescripcion(request.dia());

        if (horario.cambioEnDatos(request.dia(), request.horaInicio(), request.horaFin(), grupo)) {
            log.info("Actualizando horario con ID {}", id);

            validarHorario(grupo, dia, request.horaInicio(), request.horaFin(), id);
            horario.actualizar(request.dia(), request.horaInicio(), request.horaFin(), grupo);
            horarioRepository.saveAndFlush(horario);

            log.info("Horario con ID {} actualizado correctamente", id);
        }
        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public void eliminar(Long id) {
        Horario horario = obtenerHorario(id);
        log.info("Eliminando horario con ID {}", id);

        horarioRepository.delete(horario);
        horarioRepository.flush();
        log.info("Horario con ID {} eliminada correctamente", id);
    }

    private Horario obtenerHorario(Long id){
        return ServiceUtils.obtenerEntidadException(horarioRepository, id, Horario.class);
    }

    private Grupo obtenerGrupo(Long id) {
        return ServiceUtils.obtenerEntidadException(grupoRepository, id, Grupo.class);
    }

    private void validarHorario(Grupo grupo, DiaSemama dia, String horaInicio, String horaFin, Long idExcluir){
        if (horarioRepository.existeTraslape(dia, horaInicio, horaFin, grupo.getPeriodo(), grupo.getId(), grupo.getAula().getId(), idExcluir))
            throw new EntidadRelacionadaException("El horario se traslapa con otro del mismo grupo o aula");
    }
}
