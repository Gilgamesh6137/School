package com.damian.escuela.services;

import com.damian.escuela.dto.cursos.CursoRequest;
import com.damian.escuela.dto.cursos.CursoResponse;
import com.damian.escuela.entities.Curso;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.mapper.CursoMapper;
import com.damian.escuela.repositories.CursoRepository;
import com.damian.escuela.repositories.GrupoRepository;
import com.damian.escuela.services.cursos.CursoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CursoServiceImplTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CursoMapper cursoMapper;

    @Mock
    private GrupoRepository grupoRepository;

    @InjectMocks
    private CursoServiceImpl cursoService;

    private Curso curso;
    private CursoResponse cursoResponse;

    @BeforeEach
    void setUp() {
        curso = Curso.builder()
                .id(1L)
                .nombre("Matemáticas I")
                .descripcion("Fundamentos matemáticos")
                .creditos(6)
                .build();

        cursoResponse = new CursoResponse(1L, "Matemáticas I", "Fundamentos matemáticos", 6);
    }

    // ---------- listar() ----------

    @Test
    void listar_debeRetornarListaDeCursos_cuandoExistenRegistros() {
        when(cursoRepository.findAll()).thenReturn(List.of(curso));
        when(cursoMapper.entidadAResponse(curso)).thenReturn(cursoResponse);

        List<CursoResponse> resultado = cursoService.listar();

        assertThat(resultado).containsExactly(cursoResponse);
        verify(cursoRepository).findAll();
    }

    @Test
    void listar_debeRetornarListaVacia_cuandoNoHayCursosRegistrados() {
        when(cursoRepository.findAll()).thenReturn(List.of());
        assertThat(cursoService.listar()).isEmpty();
    }

    // ---------- obtenerPorId() ----------

    @Test
    void obtenerPorId_debeRetornarCurso_cuandoExiste() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoMapper.entidadAResponse(curso)).thenReturn(cursoResponse);

        CursoResponse resultado = cursoService.obtenerPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.creditos()).isEqualTo(6);
    }

    @Test
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cursoService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Curso no encontrado con ID: 99");
    }

    // ---------- registrar() ----------

    @Test
    void registrar_debeGuardarYRetornarCurso_cuandoElNombreNoExistePreviamente() {
        CursoRequest request = new CursoRequest("Matemáticas I", "Fundamentos matemáticos", 6);
        Curso cursoLocal = Curso.builder()
                .nombre("Matemáticas I")
                .descripcion("Fundamentos matemáticos")
                .creditos(6)
                .build();

        when(cursoRepository.existsByNombre("Matemáticas I")).thenReturn(false);
        when(cursoMapper.requestAEntidad(request)).thenReturn(cursoLocal);
        when(cursoMapper.entidadAResponse(cursoLocal)).thenReturn(cursoResponse);

        CursoResponse resultado = cursoService.registrar(request);
        assertThat(resultado).isEqualTo(cursoResponse);
        ArgumentCaptor<Curso> captor = ArgumentCaptor.forClass(Curso.class);
        verify(cursoRepository).save(captor.capture());

        assertThat(captor.getValue().getNombre()).isEqualTo("Matemáticas I");
        assertThat(captor.getValue().getDescripcion()).isEqualTo("Fundamentos matemáticos");
        assertThat(captor.getValue().getCreditos()).isEqualTo(6);
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoYaExisteUnCursoConEseNombre() {
        CursoRequest request = new CursoRequest("Matemáticas I", "Fundamentos matemáticos", 6);
        when(cursoRepository.existsByNombre("Matemáticas I")).thenReturn(true);
        assertThatThrownBy(() -> cursoService.registrar(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Nombre del curso ya existente");

        verify(cursoRepository, never()).saveAndFlush(any());
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        CursoRequest request = new CursoRequest("Mate", "desc", 6);
        when(cursoRepository.existsByNombre("Mate")).thenReturn(false);
        when(cursoMapper.requestAEntidad(request))
                .thenThrow(new DatoInvalidoException("El nombre del curso debe tener entre 5 y 50 caracteres"));
        assertThatThrownBy(() -> cursoService.registrar(request)).isInstanceOf(DatoInvalidoException.class);

        verify(cursoRepository).existsByNombre("Mate");
        verify(cursoRepository, never()).save(any());
    }

    // ---------- actualizar() ----------

    @Test
    void actualizar_debeActualizarDatos_cuandoCursoExisteYNombreEstaLibre() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.existsByNombreAndIdNot("Historia I", 1L)).thenReturn(false);

        CursoRequest request = new CursoRequest("Historia I", "Historia universal", 8);
        CursoResponse respuestaEsperada = new CursoResponse(1L, "Historia I", "Historia universal", 8);
        when(cursoMapper.entidadAResponse(curso)).thenReturn(respuestaEsperada);
        CursoResponse resultado = cursoService.actualizar(request, 1L);

        assertThat(resultado).isEqualTo(respuestaEsperada);
        assertThat(curso.getNombre()).isEqualTo("Historia I");
        assertThat(curso.getDescripcion()).isEqualTo("Historia universal");
        assertThat(curso.getCreditos()).isEqualTo(8);
        verify(cursoRepository).existsByNombreAndIdNot("Historia I", 1L);
        verify(cursoRepository).saveAndFlush(curso);
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElCursoNoExiste() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());
        CursoRequest request = new CursoRequest("Historia I", "Historia universal", 8);
        assertThatThrownBy(() -> cursoService.actualizar(request, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Curso no encontrado con ID: 99");

        verify(cursoRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.existsByNombreAndIdNot("Historia I", 1L)).thenReturn(false);

        CursoRequest request = new CursoRequest("Historia I", "desc", 0);
        assertThatThrownBy(() -> cursoService.actualizar(request, 1L)).isInstanceOf(DatoInvalidoException.class);

        verify(cursoRepository).existsByNombreAndIdNot("Historia I", 1L);
        verify(cursoRepository, never()).save(any());
    }

    // ---------- eliminar() ----------

    @Test
    void eliminar_debeEliminarCurso_cuandoExisteYNoTieneGrupos() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(grupoRepository.existsByCursoId(1L)).thenReturn(false);
        cursoService.eliminar(1L);
        verify(cursoRepository).delete(curso);
        verify(cursoRepository).flush();
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoElCursoTieneGruposAsignados() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(grupoRepository.existsByCursoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> cursoService.eliminar(1L))
                .isInstanceOf(EntidadRelacionadaException.class)
                .hasMessageContaining("No se puede eliminar este curso ya que tiene grupos asignados");

        verify(cursoRepository, never()).delete(any());
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cursoService.eliminar(99L)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(cursoRepository, never()).delete(any());
        verifyNoInteractions(grupoRepository);
    }
}
