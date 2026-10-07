package com.damian.escuela.services.cursos;

import com.damian.escuela.dto.cursos.CursoRequest;
import com.damian.escuela.dto.cursos.CursoResponse;
import com.damian.escuela.entities.Curso;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.mapper.CursoMapper;
import com.damian.escuela.repositories.CursoRepository;
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
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final CursoMapper cursoMapper;
    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {
        log.info("Listando todos los cursos");
        return cursoRepository.findAll().stream().map(cursoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtenerPorId(Long id) {
        return cursoMapper.entidadAResponse(obtenerCurso(id));
    }

    @Override
    public CursoResponse registrar(CursoRequest request) {
        validarNombreUnico(request.nombre());
        log.info("Registrando un nuevo curso");

        Curso curso = cursoMapper.requestAEntidad(request);
        cursoRepository.save(curso);

        log.info("Nuevo curso {} registrado correctamente", curso.getNombre());
        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    public CursoResponse actualizar(CursoRequest request, Long id) {
        validarcambiosNombreUnico(request.nombre(), id);
        Curso curso = obtenerCurso(id);
        log.info("Actualizando curso con ID {}", id);

        curso.actualizar(request.nombre(), request.descripcion(), request.creditos());
        cursoRepository.saveAndFlush(curso);

        log.info("Curso con ID: {} actualizado correctamente", curso.getId());
        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    public void eliminar(Long id) {
        Curso curso = obtenerCurso(id);
        log.info("Eliminando curso con ID {}", id);

        if (grupoRepository.existsByCursoId(id))
            throw new EntidadRelacionadaException("No se puede eliminar este curso ya que tiene grupos asignados");

        cursoRepository.delete(curso);
        cursoRepository.flush();
        log.info("Curso eliminado con ID: {}", curso.getId());
    }

    private Curso obtenerCurso(Long id){
        return ServiceUtils.obtenerEntidadException(cursoRepository, id, Curso.class);
    }

    private void validarNombreUnico(String nombre){
        if (cursoRepository.existsByNombre(nombre))
            throw new ConflictoException("Nombre del curso ya existente");
    }

    private void validarcambiosNombreUnico(String nombre, Long id){
        if (cursoRepository.existsByNombreAndIdNot(nombre, id))
            throw new ConflictoException("Ya existe un curso con el nombre: " + nombre);
    }
}
