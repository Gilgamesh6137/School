package com.damian.escuela.services;

import com.damian.escuela.dto.alumnos.AlumnoRequest;
import com.damian.escuela.dto.alumnos.AlumnoResponse;
import com.damian.escuela.entities.Alumno;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.mapper.AlumnoMapper;
import com.damian.escuela.repositories.AlumnoRepository;
import com.damian.escuela.repositories.InscripcionRepository;
import com.damian.escuela.services.alumnos.AlumnoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlumnoServiceImplTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    @Mock
    private AlumnoMapper alumnoMapper;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private AlumnoServiceImpl alumnoService;

    private Alumno alumno;
    private AlumnoResponse alumnoResponse;

    @BeforeEach
    void setUp() {
        alumno = Alumno.builder()
                .id(1L)
                .nombre("Carlos")
                .apellidoPaterno("González")
                .apellidoMaterno("Ramírez")
                .email("tr.2026.carlos.gonzalez.ramirez.gora260101@escuela.com.mx")
                .matricula("GORA260101")
                .build();

        alumnoResponse = new AlumnoResponse(
                1L, "Carlos González Ramírez", alumno.getEmail(), "GORA260101",
                "10/01/2026", List.of(), new BigDecimal("0.00"));
    }

    // ---------- listar() ----------

    @Test
    void listar_debeRetornarListaDeAlumnos_cuandoExistenRegistros() {
        when(alumnoRepository.findAll()).thenReturn(List.of(alumno));
        when(alumnoMapper.entidadAResponse(alumno)).thenReturn(alumnoResponse);

        List<AlumnoResponse> resultado = alumnoService.listar();

        assertThat(resultado).containsExactly(alumnoResponse);
    }

    @Test
    void listar_debeRetornarListaVacia_cuandoNoHayAlumnos() {
        when(alumnoRepository.findAll()).thenReturn(List.of());

        assertThat(alumnoService.listar()).isEmpty();
    }

    // ---------- obtenerPorId() ----------

    @Test
    void obtenerPorId_debeRetornarAlumno_cuandoExiste() {
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(alumnoMapper.entidadAResponse(alumno)).thenReturn(alumnoResponse);

        AlumnoResponse resultado = alumnoService.obtenerPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.matricula()).isEqualTo("GORA260101");
    }

    @Test
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alumnoService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Alumno no encontrado con ID: 99");
    }

    // ---------- registrar() ----------

    @Test
    void registrar_debeGenerarEmailYMatriculaConDatosRecortados_yGuardar() {
        AlumnoRequest request = new AlumnoRequest("  Carlos ", " González", "Ramírez  ");
        when(alumnoRepository.generarEmail("Carlos", "González", "Ramírez")).thenReturn("email@escuela.com.mx");
        when(alumnoRepository.generarMatricula("Carlos", "González", "Ramírez")).thenReturn("GORA260101");
        when(alumnoMapper.requestAEntidad(request, "email@escuela.com.mx", "GORA260101")).thenReturn(alumno);
        when(alumnoMapper.entidadAResponse(alumno)).thenReturn(alumnoResponse);

        AlumnoResponse resultado = alumnoService.registrar(request);

        assertThat(resultado).isEqualTo(alumnoResponse);
        verify(alumnoRepository).saveAndFlush(alumno);
    }

    // ---------- actualizar() ----------

    @Test
    void actualizar_debeRegenerarEmailYMatriculaYGuardar_cuandoCambianLosDatosPersonales() {
        AlumnoRequest request = new AlumnoRequest("Marcos", "Pérez", "Salgado");
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(alumnoRepository.generarEmail("Marcos", "Pérez", "Salgado")).thenReturn("Marcos@Escuela.com.mx");
        when(alumnoRepository.generarMatricula("Marcos", "Pérez", "Salgado")).thenReturn("PESA260102");
        when(alumnoMapper.entidadAResponse(alumno)).thenReturn(alumnoResponse);

        alumnoService.actualizar(request, 1L);

        assertThat(alumno.getNombre()).isEqualTo("Marcos");
        assertThat(alumno.getApellidoPaterno()).isEqualTo("Pérez");
        assertThat(alumno.getApellidoMaterno()).isEqualTo("Salgado");
        assertThat(alumno.getEmail()).isEqualTo("marcos@escuela.com.mx"); // normalizado a minúsculas
        assertThat(alumno.getMatricula()).isEqualTo("PESA260102");
        verify(alumnoRepository).saveAndFlush(alumno);
    }



    @Test
    void actualizar_noDebeRegenerarNiGuardar_cuandoNoCambianLosDatos() {
        Alumno alumnoMock = mock(Alumno.class);
        AlumnoRequest request = new AlumnoRequest("Carlos", "González", "Ramírez");

        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumnoMock));
        when(alumnoMapper.entidadAResponse(alumnoMock)).thenReturn(alumnoResponse);
        when(alumnoMock.cambioEnDatos("Carlos", "González", "Ramírez")).thenReturn(false);
        AlumnoResponse resultado = alumnoService.actualizar(request, 1L);
        assertThat(resultado).isEqualTo(alumnoResponse);

        verify(alumnoRepository, never()).generarEmail(anyString(), anyString(), anyString());
        verify(alumnoRepository, never()).generarMatricula(anyString(), anyString(), anyString());
        verify(alumnoRepository, never()).saveAndFlush(any());
        verify(alumnoMock, never()).actualizar(any(), any(), any(), any(), any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElAlumnoNoExiste() {
        when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

        AlumnoRequest request = new AlumnoRequest("Marcos", "Pérez", "Salgado");

        assertThatThrownBy(() -> alumnoService.actualizar(request, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Alumno no encontrado con ID: 99");

        verify(alumnoRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));

        AlumnoRequest request = new AlumnoRequest("Ana", "Pérez", "Salgado");

        assertThatThrownBy(() -> alumnoService.actualizar(request, 1L))
                .isInstanceOf(DatoInvalidoException.class);

        verify(alumnoRepository, never()).saveAndFlush(any());
    }

    // ---------- eliminar() ----------

    @Test
    void eliminar_debeEliminarAlumno_cuandoNoTieneInscripciones() {
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(inscripcionRepository.existsByAlumnoId(1L)).thenReturn(false);

        alumnoService.eliminar(1L);

        verify(alumnoRepository).delete(alumno);
        verify(alumnoRepository).flush();
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoTieneInscripciones() {
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(inscripcionRepository.existsByAlumnoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> alumnoService.eliminar(1L))
                .isInstanceOf(EntidadRelacionadaException.class)
                .hasMessageContaining("No se puede eliminar el alumno ya que tiene inscripciones asignadas")
                .hasMessageContaining("No se puede eliminar el alumno ya que tiene inscripciones asignadas");

        verify(alumnoRepository, never()).delete(any());
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alumnoService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(alumnoRepository, never()).delete(any());
        verifyNoInteractions(inscripcionRepository);
    }
}
