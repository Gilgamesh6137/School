package com.damian.escuela.services;

import com.damian.escuela.dto.maestros.MaestroRequest;
import com.damian.escuela.dto.maestros.MaestroResponse;
import com.damian.escuela.entities.Maestro;
import com.damian.escuela.exceptions.ConflictoException;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.exceptions.EntidadRelacionadaException;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.mapper.CursoMapper;
import com.damian.escuela.mapper.MaestroMapper;
import com.damian.escuela.repositories.CursoRepository;
import com.damian.escuela.repositories.GrupoRepository;
import com.damian.escuela.repositories.MaestroRepository;
import com.damian.escuela.services.maestros.MaestroServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaestroServiceImplTest {

    @Mock
    private MaestroRepository maestroRepository;

    @Mock
    private MaestroMapper maestroMapper;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CursoMapper cursoMapper;

    @Mock
    private GrupoRepository grupoRepository;

    @InjectMocks
    private MaestroServiceImpl maestroService;

    private Maestro maestro;
    private MaestroResponse maestroResponse;

    @BeforeEach
    void setUp() {
        maestro = Maestro.builder()
                .id(1L)
                .nombre("Laura")
                .apellidoPaterno("Martínez")
                .apellidoMaterno("López")
                .email("laura@escuela.com")
                .telefono("5551010789")
                .build();

        maestroResponse = new MaestroResponse(
                1L, "Laura Martínez López", "laura@escuela.com", "5551010789", List.of());
    }

    // ---------- listar() ----------

    @Test
    void listar_debeRetornarListaDeMaestros_cuandoExistenRegistros() {
        when(maestroRepository.findAll()).thenReturn(List.of(maestro));
        when(maestroMapper.entidadAResponse(maestro)).thenReturn(maestroResponse);

        List<MaestroResponse> resultado = maestroService.listar();

        assertThat(resultado).containsExactly(maestroResponse);
        verify(maestroRepository).findAll();
    }

    @Test
    void listar_debeRetornarListaVacia_cuandoNoHayMaestrosRegistrados() {
        when(maestroRepository.findAll()).thenReturn(List.of());
        assertThat(maestroService.listar()).isEmpty();
    }

    // ---------- obtenerPorId() ----------

    @Test
    void obtenerPorId_debeRetornarMaestro_cuandoExiste() {
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro));
        when(maestroMapper.entidadAResponse(maestro)).thenReturn(maestroResponse);
        MaestroResponse resultado = maestroService.obtenerPorId(1L);
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_debeLanzarExcepcion_cuandoNoExiste() {
        when(maestroRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> maestroService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Maestro no encontrado con ID: 99");
    }

    // ---------- registrar() ----------

    @Test
    void registrar_debeGuardarYRetornarMaestro_cuandoEmailYTelefonoEstanLibres() {
        MaestroRequest request = new MaestroRequest(
                "Laura", "Martínez", "López", "laura@escuela.com", "5551010789");

        when(maestroMapper.requestAEntidad(request)).thenReturn(maestro);
        when(maestroRepository.existsByEmailIgnoreCase("laura@escuela.com")).thenReturn(false);
        when(maestroRepository.existsByTelefono("5551010789")).thenReturn(false);
        when(maestroMapper.entidadAResponse(any(Maestro.class))).thenReturn(maestroResponse);

        MaestroResponse resultado = maestroService.registrar(request);

        assertThat(resultado).isEqualTo(maestroResponse);
        verify(maestroRepository).save(any(Maestro.class));
    }

    @Test
    void registrar_debeValidarUnicidadConEmailNormalizado() {
        MaestroRequest request = new MaestroRequest(
                "Laura", "Martínez", "López", "laura@escuela.com", "5551010789");

        when(maestroMapper.requestAEntidad(request)).thenReturn(maestro);
        when(maestroRepository.existsByEmailIgnoreCase("laura@escuela.com")).thenReturn(false);
        when(maestroRepository.existsByTelefono("5551010789")).thenReturn(false);
        when(maestroMapper.entidadAResponse(any(Maestro.class))).thenReturn(maestroResponse);

        maestroService.registrar(request);

        ArgumentCaptor<Maestro> captor = ArgumentCaptor.forClass(Maestro.class);
        verify(maestroRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("laura@escuela.com");
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoElEmailYaExiste() {
        MaestroRequest request = new MaestroRequest(
                "Laura", "Martínez", "López", "laura@escuela.com", "5551010789");
        when(maestroRepository.existsByEmailIgnoreCase("laura@escuela.com")).thenReturn(true);

        assertThatThrownBy(() -> maestroService.registrar(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("El email: laura@escuela.com ya está registrado");

        verify(maestroRepository, never()).save(any());
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoElTelefonoYaExiste() {
        MaestroRequest request = new MaestroRequest(
                "Laura", "Martínez", "López", "laura@escuela.com", "5551010789");

        when(maestroRepository.existsByEmailIgnoreCase("laura@escuela.com")).thenReturn(false);
        when(maestroRepository.existsByTelefono("5551010789")).thenReturn(true);

        assertThatThrownBy(() -> maestroService.registrar(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("El teléfono: 5551010789 ya está registrado");

        verify(maestroRepository, never()).saveAndFlush(any());
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        MaestroRequest request = new MaestroRequest(
                "Ana", "Martínez", "López",
                "laura@escuela.com",
                "5551010789"
        );

        when(maestroRepository.existsByEmailIgnoreCase("laura@escuela.com")).thenReturn(false);
        when(maestroRepository.existsByTelefono("5551010789")).thenReturn(false);
        when(maestroMapper.requestAEntidad(request)).thenThrow(new DatoInvalidoException("El nombre es requerido"));

        assertThatThrownBy(() -> maestroService.registrar(request)).isInstanceOf(DatoInvalidoException.class);
        verify(maestroRepository).existsByEmailIgnoreCase("laura@escuela.com");
        verify(maestroRepository).existsByTelefono("5551010789");
        verify(maestroRepository, never()).save(any());
    }

    // ---------- actualizar() ----------

    @Test
    void actualizar_debeActualizarDatos_cuandoMaestroExisteYDatosUnicosEstanLibres() {
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro));
        when(maestroRepository.existsByEmailIgnoreCaseAndIdNot("karla@escuela.com", 1L)).thenReturn(false);
        when(maestroRepository.existsByTelefonoAndIdNot("5559999999", 1L)).thenReturn(false);

        MaestroRequest request = new MaestroRequest(
                "Karla", "Gómez", "Pérez", "karla@escuela.com", "5559999999");
        MaestroResponse respuestaEsperada = new MaestroResponse(
                1L, "Karla Gómez Pérez", "karla@escuela.com", "5559999999", List.of());
        when(maestroMapper.entidadAResponse(maestro)).thenReturn(respuestaEsperada);

        MaestroResponse resultado = maestroService.actualizar(request, 1L);
        assertThat(resultado.nombre()).isEqualTo("Karla Gómez Pérez");
        assertThat(maestro.getNombre()).isEqualTo("Karla");
        assertThat(maestro.getEmail()).isEqualTo("karla@escuela.com");
        assertThat(maestro.getTelefono()).isEqualTo("5559999999");
        verify(maestroRepository).saveAndFlush(maestro);
    }

    @Test
    void actualizar_noDebeValidarNiGuardar_cuandoNoHayCambios() {
        when(maestroRepository.findById(anyLong())).thenReturn(Optional.of(maestro));
        when(maestroRepository.existsByEmailIgnoreCaseAndIdNot("laura@escuela.com", 1L)).thenReturn(false);
        when(maestroRepository.existsByTelefonoAndIdNot("5551010789", 1L)).thenReturn(false);
        when(maestroMapper.entidadAResponse(maestro)).thenReturn(maestroResponse);

        MaestroRequest request = new MaestroRequest(
                "Laura", "Martínez", "López", "laura@escuela.com", "5551010789");

        MaestroResponse resultado = maestroService.actualizar(request, 1L);
        assertThat(resultado).isEqualTo(maestroResponse);

        verify(maestroRepository).existsByEmailIgnoreCaseAndIdNot("laura@escuela.com", 1L);
        verify(maestroRepository).existsByTelefonoAndIdNot("5551010789", 1L);
        verify(maestroRepository, never()).save(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElMaestroNoExiste() {
        when(maestroRepository.findById(99L)).thenReturn(Optional.empty());

        MaestroRequest request = new MaestroRequest(
                "Karla", "Gómez", "Pérez", "karla@escuela.com", "5559999999");
        assertThatThrownBy(() -> maestroService.actualizar(request, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Maestro no encontrado con ID: 99");

        verify(maestroRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElNuevoEmailYaLoUsaOtroMaestro() {
        Maestro maestroReal = Maestro.crear("Laura", "Martínez", "López", "laura@escuela.com", "5551010789");
        lenient().when(maestroRepository.findById(anyLong())).thenReturn(Optional.of(maestroReal));
        lenient().when(maestroRepository.existsByEmailIgnoreCase("otro@escuela.com")).thenReturn(true);
        lenient().when(maestroRepository.existsByEmailIgnoreCaseAndIdNot(eq("otro@escuela.com"), anyLong())).thenReturn(true);

        MaestroRequest request = new MaestroRequest(
                "Karla", "Gómez", "Pérez", "otro@escuela.com", "5559999999");
        assertThatThrownBy(() -> maestroService.actualizar(request, 1L))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("El email: otro@escuela.com ya está registrado");

        verify(maestroRepository, never()).save(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElNuevoTelefonoYaLoUsaOtroMaestro() {
        Maestro maestroReal = Maestro.crear("Laura", "Martínez", "López", "laura@escuela.com", "5551010789");
        lenient().when(maestroRepository.findById(anyLong())).thenReturn(Optional.of(maestroReal));
        lenient().when(maestroRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        lenient().when(maestroRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), anyLong())).thenReturn(false);
        lenient().when(maestroRepository.existsByTelefono("5559999999")).thenReturn(true);
        lenient().when(maestroRepository.existsByTelefonoAndIdNot(eq("5559999999"), anyLong())).thenReturn(true);

        MaestroRequest request = new MaestroRequest(
                "Karla", "Gómez", "Pérez", "karla@escuela.com", "5559999999");
        assertThatThrownBy(() -> maestroService.actualizar(request, 1L))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("El teléfono: 5559999999 ya está registrado");

        assertThat(maestroReal.getNombre()).isEqualTo("Laura"); // Sin cambios
        verify(maestroRepository, never()).save(any());
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro));
        MaestroRequest request = new MaestroRequest(
                "Karla", "Gómez", "Pérez", "karla@escuela.com", "123");

        assertThatThrownBy(() -> maestroService.actualizar(request, 1L)).isInstanceOf(DatoInvalidoException.class);
        verify(maestroRepository, never()).saveAndFlush(any());
    }

    @Test
    void actualizar_debeValidarUnicidadConEmailNormalizado() {
        when(maestroRepository.findById(anyLong())).thenReturn(Optional.of(maestro));
        when(maestroRepository.existsByEmailIgnoreCaseAndIdNot(anyString(), eq(1L))).thenReturn(false);
        when(maestroRepository.existsByTelefonoAndIdNot("5559999999", 1L)).thenReturn(false);
        when(maestroMapper.entidadAResponse(maestro)).thenReturn(maestroResponse);

        MaestroRequest request = new MaestroRequest(
                "Karla", "Gómez", "Pérez", "  KARLA@Escuela.com ", "5559999999");

        maestroService.actualizar(request, 1L);
        verify(maestroRepository).existsByEmailIgnoreCaseAndIdNot("  KARLA@Escuela.com ", 1L);
        verify(maestroRepository).saveAndFlush(maestro);
    }

    // ---------- eliminar() ----------

    @Test
    void eliminar_debeEliminarMaestro_cuandoExisteYNoTieneGruposAsignados() {
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro));
        when(grupoRepository.existsByMaestroId(1L)).thenReturn(false);
        maestroService.eliminar(1L);

        verify(maestroRepository).delete(maestro);
        verify(maestroRepository).flush();
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoElMaestroTieneGruposAsignados() {
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro));
        when(grupoRepository.existsByMaestroId(1L)).thenReturn(true);
        assertThatThrownBy(() -> maestroService.eliminar(1L))
                .isInstanceOf(EntidadRelacionadaException.class)
                .hasMessageContaining("No se puede eliminar el maestro ya que tiene grupos asignados");

        verify(maestroRepository, never()).delete(any());
    }

    @Test
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(maestroRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> maestroService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(maestroRepository, never()).delete(any());
        verifyNoInteractions(grupoRepository);
    }
}
