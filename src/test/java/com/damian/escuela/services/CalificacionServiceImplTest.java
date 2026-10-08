package com.damian.escuela.services;

import com.damian.escuela.dto.calificacion.CalificacionRequest;
import com.damian.escuela.dto.calificacion.CalificacionResponse;
import com.damian.escuela.dto.datos.DatosAlumno;
import com.damian.escuela.dto.datos.DatosGrupo;
import com.damian.escuela.dto.datos.DatosInscripcion;
import com.damian.escuela.entities.Calificacion;
import com.damian.escuela.entities.Inscripcion;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.mapper.CalificacionMapper;
import com.damian.escuela.repositories.CalificacionRepository;
import com.damian.escuela.repositories.InscripcionRepository;
import com.damian.escuela.services.calificacion.CalificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class CalificacionServiceImplTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @Mock
    private CalificacionMapper calificacionMapper;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private CalificacionServiceImpl calificacionService;

    private Inscripcion inscripcion;
    private Calificacion calificacion;
    private CalificacionResponse calificacionResponse;

    @BeforeEach
    void setUp() {
        inscripcion = Inscripcion.builder().id(15L).build();

        calificacion = Calificacion.builder()
                .id(2L)
                .inscripcion(inscripcion)
                .calificacion(new BigDecimal("8.5"))
                .fechaRegistro(LocalDate.of(2026, 2, 11))
                .build();

        DatosInscripcion datosInscripcion = new DatosInscripcion(
                new DatosAlumno("María Gómez Ramos", "A2025002", "maria.gomez@alumnos.com", "11/01/2025"),
                new DatosGrupo("Matemáticas I", "Laura Martínez Martínez", "Aula 101", "2025-01"),
                "11/02/2026");

        calificacionResponse = new CalificacionResponse(
                2L, datosInscripcion, new BigDecimal("8.5"), "11/02/2026");
    }

    // ---------- listar() ----------

    @Test
    void listar_debeRetornarListaDeCalificaciones_cuandoExistenRegistros() {
        when(calificacionRepository.findAll()).thenReturn(List.of(calificacion));
        when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(calificacionResponse);
        List<CalificacionResponse> resultado = calificacionService.listar();
        assertThat(resultado).containsExactly(calificacionResponse);
    }

    @Test
    void listar_debeRetornarListaVacia_cuandoNoHayCalificaciones() {
        when(calificacionRepository.findAll()).thenReturn(List.of());
        assertThat(calificacionService.listar()).isEmpty();
    }

    // ---------- obtenerPorId() ----------

    @Test
    void obtenerPorId_debeRetornarCalificacion_cuandoExiste() {
        when(calificacionRepository.findById(2L)).thenReturn(Optional.of(calificacion));
        when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(calificacionResponse);

        CalificacionResponse resultado = calificacionService.obtenerPorId(2L);

        assertThat(resultado.id()).isEqualTo(2L);
        assertThat(resultado.calificacion()).isEqualByComparingTo("8.5");
    }

    @Test
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(calificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> calificacionService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Calificacion no encontrado con ID: 99");
    }

    // ---------- registrar() ----------

    @Test
    void registrar_debeGuardarYRetornarCalificacion_cuandoLaInscripcionNoTieneCalificacion() {
        CalificacionRequest request = new CalificacionRequest(15L, new BigDecimal("8.5"));
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.of(inscripcion));
        when(calificacionRepository.existsByInscripcionId(15L)).thenReturn(false);
        when(calificacionMapper.requestAEntidad(request, inscripcion)).thenReturn(calificacion);
        when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(calificacionResponse);

        CalificacionResponse resultado = calificacionService.registrar(request);

        assertThat(resultado).isEqualTo(calificacionResponse);
        verify(calificacionRepository).saveAndFlush(calificacion);
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoLaInscripcionNoExiste() {
        CalificacionRequest request = new CalificacionRequest(99L, new BigDecimal("8.5"));
        when(inscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> calificacionService.registrar(request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Inscripcion no encontrado con ID: 99");

        verify(calificacionRepository, never()).saveAndFlush(any());
        verifyNoInteractions(calificacionMapper);
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoLaInscripcionYaTieneCalificacion() {
        CalificacionRequest request = new CalificacionRequest(15L, new BigDecimal("8.5"));
        when(inscripcionRepository.findById(15L)).thenReturn(Optional.of(inscripcion));
        when(calificacionRepository.existsByInscripcionId(15L)).thenReturn(true);

        assertThatThrownBy(() -> calificacionService.registrar(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("La inscripción ya tiene una calificación registrada");

        verify(calificacionRepository, never()).saveAndFlush(any());
        verifyNoInteractions(calificacionMapper);
    }

    // ---------- actualizar() ----------

    @Test
    void actualizar_debeModificarLaCalificacionYGuardar_cuandoEsValida() {
        CalificacionRequest request = new CalificacionRequest(15L, new BigDecimal("9.0"));
        when(calificacionRepository.findById(2L)).thenReturn(Optional.of(calificacion));
        when(calificacionMapper.entidadAResponse(calificacion)).thenReturn(calificacionResponse);

        CalificacionResponse resultado = calificacionService.actualizar(request, 2L);

        assertThat(resultado).isEqualTo(calificacionResponse);
        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("9.0");
        verify(calificacionRepository).saveAndFlush(calificacion);
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoLaCalificacionNoExiste() {
        CalificacionRequest request = new CalificacionRequest(15L, new BigDecimal("9.0"));
        when(calificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> calificacionService.actualizar(request, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Calificacion no encontrado con ID: 99");

        verify(calificacionRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_noDebeGuardar_cuandoLaCalificacionEstaFueraDeRango() {
        CalificacionRequest request = new CalificacionRequest(15L, new BigDecimal("10.5"));
        when(calificacionRepository.findById(2L)).thenReturn(Optional.of(calificacion));

        assertThatThrownBy(() -> calificacionService.actualizar(request, 2L))
                .isInstanceOf(DatoInvalidoException.class);

        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("8.5");
        verify(calificacionRepository, never()).saveAndFlush(any());
    }

    // ---------- eliminar() ----------

    @Test
    void eliminar_debeEliminarCalificacion_cuandoExiste() {
        when(calificacionRepository.findById(2L)).thenReturn(Optional.of(calificacion));
        calificacionService.eliminar(2L);
        verify(calificacionRepository).delete(calificacion);
        verify(calificacionRepository).flush();
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(calificacionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> calificacionService.eliminar(99L)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(calificacionRepository, never()).delete(any());
    }
}
