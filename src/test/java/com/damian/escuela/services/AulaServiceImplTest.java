package com.damian.escuela.services;

import com.damian.escuela.dto.aulas.AulaRequest;
import com.damian.escuela.dto.aulas.AulaResponse;
import com.damian.escuela.entities.Aula;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.mapper.AulaMapper;
import com.damian.escuela.repositories.AulaRepository;
import com.damian.escuela.repositories.GrupoRepository;
import com.damian.escuela.services.aulas.AulaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AulaServiceImplTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private AulaMapper aulaMapper;

    @Mock
    private GrupoRepository grupoRepository;

    @InjectMocks
    private AulaServiceImpl aulaService;

    private Aula aula;
    private AulaResponse aulaResponse;

    @BeforeEach
    void setUp() {
        aula = Aula.builder().id(1L).nombre("Aula 101").capacidad(30).build();
        aulaResponse = new AulaResponse(1L, "Aula 101", 30);
    }

    // ---------- listar() ----------

    @Test
    void listar_debeRetornarListaDeAulas_cuandoExistenRegistros() {
        when(aulaRepository.findAll()).thenReturn(List.of(aula));
        when(aulaMapper.entidadAResponse(aula)).thenReturn(aulaResponse);

        List<AulaResponse> resultado = aulaService.listar();

        assertThat(resultado).containsExactly(aulaResponse);
        verify(aulaRepository).findAll();
    }

    @Test
    void listar_debeRetornarListaVacia_cuandoNoHayAulasRegistradas() {
        when(aulaRepository.findAll()).thenReturn(List.of());
        List<AulaResponse> resultado = aulaService.listar();
        assertThat(resultado).isEmpty();
    }

    // ---------- obtenerPorId() ----------

    @Test
    void obtenerPorId_debeRetornarAula_cuandoExiste() {
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
        when(aulaMapper.entidadAResponse(aula)).thenReturn(aulaResponse);

        AulaResponse resultado = aulaService.obtenerPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.capacidad()).isEqualTo(30);
    }

    @Test
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(aulaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aulaService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Aula no encontrado con ID: 99");
    }

    // ---------- registrar() ----------

    @Test
    void registrar_debeGuardarYRetornarAula_cuandoElNombreNoExistePreviamente() {
        AulaRequest request = new AulaRequest("Aula 101", 30);
        when(aulaMapper.requestAEntidad(request)).thenReturn(aula);
        when(aulaRepository.existsByNombre("Aula 101")).thenReturn(false);
        when(aulaMapper.entidadAResponse(aula)).thenReturn(aulaResponse);

        AulaResponse resultado = aulaService.registrar(request);
        assertThat(resultado).isEqualTo(aulaResponse);

        verify(aulaRepository).existsByNombre("Aula 101");
        verify(aulaRepository).save(aula);
        assertThat(aula.getNombre()).isEqualTo("Aula 101");
        assertThat(aula.getCapacidad()).isEqualTo(30);
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoYaExisteUnaAulaConEseNombre() {
        AulaRequest request = new AulaRequest("Aula 101", 30);
        when(aulaRepository.existsByNombre("Aula 101")).thenReturn(true);

        assertThatThrownBy(() -> aulaService.registrar(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("El aula con el nombre: Aula 101 ya existe");

        verify(aulaRepository, never()).save(any());
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        AulaRequest request = new AulaRequest("Ab", 30);
        when(aulaMapper.requestAEntidad(request))
                .thenThrow(new DatoInvalidoException("El nombre es requerido y debe tener entre 1 y 30 caracteres"));

        assertThatThrownBy(() -> aulaService.registrar(request)).isInstanceOf(DatoInvalidoException.class);
        verify(aulaRepository).existsByNombre(anyString());
        verify(aulaRepository, never()).save(any());
    }

    // ---------- actualizar() ----------

    @Test
    void actualizar_debeActualizarDatos_cuandoAulaExisteYNombreEstaLibre() {
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
        when(aulaRepository.existsByNombreAndIdNot("Aula 202", 1L)).thenReturn(false);

        AulaRequest request = new AulaRequest("Aula 202", 40);
        AulaResponse respuestaEsperada = new AulaResponse(1L, "Aula 202", 40);
        when(aulaMapper.entidadAResponse(aula)).thenReturn(respuestaEsperada);

        AulaResponse resultado = aulaService.actualizar(request, 1L);

        assertThat(resultado.nombre()).isEqualTo("Aula 202");
        assertThat(aula.getNombre()).isEqualTo("Aula 202");
        assertThat(aula.getCapacidad()).isEqualTo(40);
        verify(aulaRepository).saveAndFlush(aula);
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElAulaNoExiste() {
        when(aulaRepository.findById(99L)).thenReturn(Optional.empty());
        AulaRequest request = new AulaRequest("Aula 202", 40);

        assertThatThrownBy(() -> aulaService.actualizar(request, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Aula no encontrado con ID: 99");

        verify(aulaRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
        AulaRequest request = new AulaRequest("Aula 202", 0);
        assertThatThrownBy(() -> aulaService.actualizar(request, 1L)).isInstanceOf(DatoInvalidoException.class);

        verify(aulaRepository).existsByNombreAndIdNot(anyString(), anyLong());
        verify(aulaRepository, never()).saveAndFlush(any());
    }

    // ---------- eliminar() ----------

    @Test
    void eliminar_debeEliminarAula_cuandoExisteYNoTieneGrupos() {
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
        when(grupoRepository.existsByAulaId(1L)).thenReturn(false);
        aulaService.eliminar(1L);

        verify(aulaRepository).delete(aula);
        verify(aulaRepository).flush();
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoElAulaTieneGruposAsignados() {
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula));
        when(grupoRepository.existsByAulaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> aulaService.eliminar(1L))
                .isInstanceOf(EntidadRelacionadaException.class)
                .hasMessageContaining("No se puede eliminar el aula ya que tiene grupos asignados");

        verify(aulaRepository, never()).delete(any());
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(aulaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> aulaService.eliminar(99L)).isInstanceOf(RecursoNoEncontradoException.class);

        verify(aulaRepository, never()).delete(any());
        verifyNoInteractions(grupoRepository);
    }
}
