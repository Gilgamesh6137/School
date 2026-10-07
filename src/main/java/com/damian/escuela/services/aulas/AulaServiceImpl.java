package com.damian.escuela.services.aulas;

import com.damian.escuela.dto.aulas.AulaRequest;
import com.damian.escuela.dto.aulas.AulaResponse;
import com.damian.escuela.entities.Aula;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.mapper.AulaMapper;
import com.damian.escuela.repositories.AulaRepository;
import com.damian.escuela.repositories.GrupoRepository;
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
public class AulaServiceImpl implements AulaService{

    private final AulaRepository aulaRepository;
    private final AulaMapper aulaMapper;
    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AulaResponse> listar() {
        log.info("Listando todas las aulas");
        return aulaRepository.findAll().stream().map(aulaMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AulaResponse obtenerPorId(Long id) {
        return aulaMapper.entidadAResponse(obtenerAula(id));
    }

    @Override
    public AulaResponse registrar(AulaRequest request) {
        validarNombreUnico(request.nombre());
        log.info("Registrando una nueva aula");

        Aula aula = aulaMapper.requestAEntidad(request);
        aulaRepository.save(aula);

        log.info("Nueva aula {} registrada correctamente", aula.getId());
        return aulaMapper.entidadAResponse(aula);
    }

    @Override
    public AulaResponse actualizar(AulaRequest request, Long id) {
        validarCambiosNombreUnico(request.nombre(), id);
        Aula aula = obtenerAula(id);
        log.info("Actualizando aula con ID {}", id);

        aula.actualizar(request.nombre(), request.capacidad());
        aulaRepository.saveAndFlush(aula);

        log.info("Aula con ID {} actualizada correctamente", id);
        return aulaMapper.entidadAResponse(aula);
    }

    @Override
    public void eliminar(Long id) {
        Aula aula = obtenerAula(id);
        log.info("Eliminando aula con ID {}", id);

        if (grupoRepository.existsByAulaId(id))
            throw new EntidadRelacionadaException("No se puede eliminar el aula ya que tiene grupos asignados");

        aulaRepository.delete(aula);
        aulaRepository.flush();
        log.info("Aula con ID {} eliminada correctamente", id);
    }

    private Aula obtenerAula(Long id){
        return ServiceUtils.obtenerEntidadException(aulaRepository, id, Aula.class);
    }

    private void validarNombreUnico(String nombre){
        if (aulaRepository.existsByNombre(nombre)){
            throw new ConflictoException("El aula con el nombre: " + nombre + " ya existe");
        }
    }

    private void validarCambiosNombreUnico(String nombre, Long id){
        if (aulaRepository.existsByNombreAndIdNot(nombre, id)){
            throw new ConflictoException("El aula con el nombre: " + nombre + " ya existe");
        }
    }
}
