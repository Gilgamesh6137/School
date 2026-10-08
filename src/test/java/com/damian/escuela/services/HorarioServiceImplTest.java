package com.damian.escuela.services;

import com.damian.escuela.dto.datos.DatosGrupo;
import com.damian.escuela.dto.horarios.HorarioRequest;
import com.damian.escuela.dto.horarios.HorarioResponse;
import com.damian.escuela.entities.Aula;
import com.damian.escuela.entities.Grupo;
import com.damian.escuela.entities.Horario;
import com.damian.escuela.enums.DiaSemama;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.mapper.HorarioMapper;
import com.damian.escuela.repositories.GrupoRepository;
import com.damian.escuela.repositories.HorarioRepository;
import com.damian.escuela.services.horarios.HorarioServiceImpl;
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
public class HorarioServiceImplTest {

    private static final long SIN_EXCLUIR = -1L;

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private HorarioMapper horarioMapper;

    @Mock
    private GrupoRepository grupoRepository;

    @InjectMocks
    private HorarioServiceImpl horarioService;

    private Aula aula;
    private Grupo grupo;
    private Horario horario;
    private HorarioResponse horarioResponse;

    @BeforeEach
    void setUp() {

        aula = Aula.builder()
                .id(3L)
                .nombre("Aula 101")
                .capacidad(30)
                .build();

        grupo = Grupo.builder()
                .id(5L)
                .aula(aula)
                .periodo("2026-01")
                .build();

        horario = Horario.builder()
                .id(1L)
                .grupo(grupo)
                .diaSemama(DiaSemama.LUNES)
                .horaInicio("08:00")
                .horaFin("10:00")
                .build();

        horarioResponse = new HorarioResponse(
                1L,
                new DatosGrupo(
                        "Matemáticas I",
                        "Laura Martínez Martínez",
                        "Aula 101",
                        "2026-01"
                ),
                "Lunes 08:00 - 10:00"
        );
    }

    // =========================================================
    // listar()
    // =========================================================

    @Test
    void listar_debeRetornarListaDeHorarios_cuandoExistenRegistros() {
        when(horarioRepository.findAll()).thenReturn(List.of(horario));
        when(horarioMapper.entidadAResponse(horario)).thenReturn(horarioResponse);
        List<HorarioResponse> resultado = horarioService.listar();
        assertThat(resultado).containsExactly(horarioResponse);
        verify(horarioRepository).findAll();
        verify(horarioMapper).entidadAResponse(horario);
    }

    @Test
    void listar_debeRetornarListaVacia_cuandoNoHayHorarios() {
        when(horarioRepository.findAll()).thenReturn(List.of());
        List<HorarioResponse> resultado = horarioService.listar();
        assertThat(resultado).isEmpty();
        verify(horarioRepository).findAll();
        verifyNoInteractions(horarioMapper);
    }

    // =========================================================
    // obtenerPorId()
    // =========================================================

    @Test
    void obtenerPorId_debeRetornarHorario_cuandoExiste() {
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(horarioMapper.entidadAResponse(horario)).thenReturn(horarioResponse);
        HorarioResponse resultado = horarioService.obtenerPorId(1L);
        assertThat(resultado).isEqualTo(horarioResponse);
        verify(horarioRepository).findById(1L);
        verify(horarioMapper).entidadAResponse(horario);
    }

    @Test
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(horarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> horarioService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Horario no encontrado con ID: 99");

        verifyNoInteractions(horarioMapper);
    }

    // =========================================================
    // registrar()
    // =========================================================

    @Test
    void registrar_debeGuardarYRetornarHorario_cuandoNoHayTraslape() {
        HorarioRequest request = new HorarioRequest(5L, "Lunes", "08:00", "10:00");
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        when(horarioMapper.requestAEntidad(request, grupo)).thenReturn(horario);
        when(horarioRepository.existeTraslape(
                DiaSemama.LUNES,
                "08:00",
                "10:00",
                "2026-01",
                5L,
                3L,
                SIN_EXCLUIR
        )).thenReturn(false);

        when(horarioMapper.entidadAResponse(horario)).thenReturn(horarioResponse);
        HorarioResponse resultado = horarioService.registrar(request);
        assertThat(resultado).isEqualTo(horarioResponse);
        verify(grupoRepository).findById(5L);
        verify(horarioMapper).requestAEntidad(request, grupo);
        verify(horarioRepository).existeTraslape(
                DiaSemama.LUNES,
                "08:00",
                "10:00",
                "2026-01",
                5L,
                3L,
                SIN_EXCLUIR
        );
        verify(horarioRepository).saveAndFlush(horario);
    }

    @Test
    void registrar_debeResolverElDiaIgnorandoAcentosYMayusculas() {
        Horario horarioMiercoles = Horario.builder()
                .id(2L)
                .grupo(grupo)
                .diaSemama(DiaSemama.MIERCOLES)
                .horaInicio("08:00")
                .horaFin("10:00")
                .build();

        HorarioRequest request = new HorarioRequest(5L, "MIÉRCOLES", "08:00", "10:00");
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        when(horarioMapper.requestAEntidad(request, grupo)).thenReturn(horarioMiercoles);
        when(horarioRepository.existeTraslape(
                DiaSemama.MIERCOLES,
                "08:00",
                "10:00",
                "2026-01",
                5L,
                3L,
                SIN_EXCLUIR
        )).thenReturn(false);

        when(horarioMapper.entidadAResponse(horarioMiercoles)).thenReturn(horarioResponse);
        horarioService.registrar(request);
        verify(horarioMapper).requestAEntidad(request, grupo);
        verify(horarioRepository).existeTraslape(
                DiaSemama.MIERCOLES,
                "08:00",
                "10:00",
                "2026-01",
                5L,
                3L,
                SIN_EXCLUIR
        );
        verify(horarioRepository).saveAndFlush(horarioMiercoles);
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoElGrupoNoExiste() {
        HorarioRequest request = new HorarioRequest(99L, "Lunes", "08:00", "10:00");
        when(grupoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> horarioService.registrar(request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Grupo no encontrado con ID: 99");

        verifyNoInteractions(horarioMapper);
        verify(horarioRepository, never()).saveAndFlush(any());
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoElDiaNoExiste() {
        HorarioRequest request = new HorarioRequest(5L, "Domingo", "08:00", "10:00");
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        assertThatThrownBy(() -> horarioService.registrar(request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Domingo");

        verifyNoInteractions(horarioMapper);
        verify(horarioRepository, never()).existeTraslape(any(), anyString(), anyString(), anyString(), anyLong(), anyLong(), anyLong());
        verify(horarioRepository, never()).save(any());
    }

    // =========================================================
    // actualizar()
    // =========================================================

    @Test
    void actualizar_noDebeValidarNiGuardar_cuandoNoHayCambios() {
        HorarioRequest request = new HorarioRequest(5L, "Lunes", "08:00", "10:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        when(horarioMapper.entidadAResponse(horario)).thenReturn(horarioResponse);

        HorarioResponse resultado = horarioService.actualizar(request, 1L);
        assertThat(resultado).isEqualTo(horarioResponse);

        verify(horarioRepository, never()).existeTraslape(any(), anyString(), anyString(), anyString(), anyLong(), anyLong(), anyLong());
        verify(horarioRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeActualizarYGuardar_cuandoCambiaDiaYHorasSinTraslape() {
        HorarioRequest request = new HorarioRequest(5L, "Viernes", "12:00", "14:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        when(horarioRepository.existeTraslape(
                DiaSemama.VIERNES,
                "12:00",
                "14:00",
                "2026-01",
                5L,
                3L,
                1L
        )).thenReturn(false);

        when(horarioMapper.entidadAResponse(horario)).thenReturn(horarioResponse);
        HorarioResponse resultado = horarioService.actualizar(request, 1L);
        assertThat(resultado).isEqualTo(horarioResponse);
        assertThat(horario.getDiaSemama()).isEqualTo(DiaSemama.VIERNES);
        assertThat(horario.getHoraInicio()).isEqualTo("12:00");
        assertThat(horario.getHoraFin()).isEqualTo("14:00");
        assertThat(horario.getGrupo()).isSameAs(grupo);
        verify(horarioRepository).saveAndFlush(horario);
    }

    @Test
    void actualizar_debeActualizarElGrupo_cuandoCambiaElGrupo() {
        Aula otraAula = Aula.builder().id(4L).nombre("Laboratorio A").capacidad(28).build();
        Grupo otroGrupo = Grupo.builder().id(6L).aula(otraAula).periodo("2026-02").build();
        HorarioRequest request = new HorarioRequest(6L, "Lunes", "08:00", "10:00");

        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(grupoRepository.findById(6L)).thenReturn(Optional.of(otroGrupo));
        when(horarioRepository.existeTraslape(
                DiaSemama.LUNES,
                "08:00",
                "10:00",
                "2026-02",
                6L,
                4L,
                1L
        )).thenReturn(false);

        when(horarioMapper.entidadAResponse(horario)).thenReturn(horarioResponse);
        horarioService.actualizar(request, 1L);
        assertThat(horario.getGrupo()).isSameAs(otroGrupo);
        verify(horarioRepository).existeTraslape(
                        DiaSemama.LUNES,
                        "08:00",
                        "10:00",
                        "2026-02",
                        6L,
                        4L,
                        1L
                );
        verify(horarioRepository).saveAndFlush(horario);
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoLaHoraFinNoEsPosterior() {
        HorarioRequest request = new HorarioRequest(5L, "Lunes", "10:00", "08:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        assertThatThrownBy(() -> horarioService.actualizar(request, 1L))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La hora de inicio debe ser menor a la hora de fin");

        verify(horarioRepository, never()).existeTraslape(
                        any(), anyString(), anyString(), anyString(), anyLong(), anyLong(), anyLong()
        );
        verify(horarioRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElDiaNoExiste() {
        HorarioRequest request = new HorarioRequest(5L, "Domingo", "08:00", "10:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        assertThatThrownBy(() -> horarioService.actualizar(request, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Domingo");

        verify(horarioRepository, never()).existeTraslape(
                any(), anyString(), anyString(), anyString(), anyLong(), anyLong(), anyLong()
        );
        verify(horarioRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElHorarioNoExiste() {
        HorarioRequest request = new HorarioRequest(5L, "Lunes", "08:00", "10:00");
        when(horarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> horarioService.actualizar(request, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Horario no encontrado con ID: 99");

        verifyNoInteractions(grupoRepository);
        verifyNoInteractions(horarioMapper);
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElGrupoNoExiste() {
        HorarioRequest request = new HorarioRequest(99L, "Lunes", "08:00", "10:00");
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(grupoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> horarioService.actualizar(request, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Grupo no encontrado con ID: 99");

        verify(horarioRepository, never()).saveAndFlush(any());
        verifyNoInteractions(horarioMapper);
    }

    // =========================================================
    // eliminar()
    // =========================================================

    @Test
    void eliminar_debeEliminarHorario_cuandoExiste() {
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        horarioService.eliminar(1L);

        verify(horarioRepository).findById(1L);
        verify(horarioRepository).delete(horario);
        verify(horarioRepository).flush();
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(horarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> horarioService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Horario no encontrado con ID: 99");

        verify(horarioRepository, never()).delete(any());
        verify(horarioRepository, never()).flush();
    }
}
