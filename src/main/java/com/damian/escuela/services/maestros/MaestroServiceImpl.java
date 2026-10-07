package com.damian.escuela.services.maestros;

import com.damian.escuela.dto.maestros.MaestroRequest;
import com.damian.escuela.dto.maestros.MaestroResponse;
import com.damian.escuela.entities.Maestro;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.mapper.MaestroMapper;
import com.damian.escuela.repositories.GrupoRepository;
import com.damian.escuela.repositories.MaestroRepository;
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
public class MaestroServiceImpl implements MaestroService{

    private final MaestroRepository maestroRepository;
    private final MaestroMapper maestroMapper;
    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MaestroResponse> listar() {
        log.info("Listando todos los maestros");
        return maestroRepository.findAll().stream().map(maestroMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaestroResponse obtenerPorId(Long id) {
        return maestroMapper.entidadAResponse(obtenerMaestro(id));
    }

    @Override
    public MaestroResponse registrar(MaestroRequest request) {
        validarEmailYTelefonoUnico(request);
        log.info("Registrando un nuevo maestro");

        Maestro maestro = maestroMapper.requestAEntidad(request);
        maestroRepository.save(maestro);

        log.info("Nuevo maestro {} registrado correctamente", maestro.getNombre());
        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    public MaestroResponse actualizar(MaestroRequest request, Long id) {
        validarCambiosEmailYTelefonoUnico(request, id);
        Maestro maestro = obtenerMaestro(id);
        log.info("Actualizando maestro con ID {}", id);

        maestro.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email(),
                request.telefono()
        );
        maestroRepository.saveAndFlush(maestro);

        log.info("Maestro con ID {} actualizado correctamente", id);
        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    public void eliminar(Long id) {
        Maestro maestro = obtenerMaestro(id);
        log.info("Eliminando maestro con ID {}", id);

        if (grupoRepository.existsByMaestroId(id))
            throw new EntidadRelacionadaException("No se puede eliminar el maestro ya que tiene grupos asignados");

        maestroRepository.delete(maestro);
        maestroRepository.flush();
        log.info("Maestro con ID {} eliminado correctamente", id);
    }

    private Maestro obtenerMaestro(Long id){
        return ServiceUtils.obtenerEntidadException(maestroRepository, id, Maestro.class);
    }

    private void validarEmailYTelefonoUnico(MaestroRequest request){
        log.info("Validando email y teléfono únicos");

        if (maestroRepository.existsByEmailIgnoreCase(request.email().trim()))
            throw new ConflictoException("El email: " + request.email() + " ya está registrado");

        if (maestroRepository.existsByTelefono(request.telefono().trim()))
            throw new ConflictoException("El teléfono: " + request.telefono() + " ya está registrado");
    }

    private void validarCambiosEmailYTelefonoUnico(MaestroRequest request, Long id){
        log.info("Validando cambios en el email y teléfono únicos");

        if (maestroRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id))
            throw new ConflictoException("El email: " + request.email() + " ya está registrado");

        if (maestroRepository.existsByTelefonoAndIdNot(request.telefono(), id))
            throw new ConflictoException("El teléfono: " + request.telefono() + " ya está registrado");
    }
}
