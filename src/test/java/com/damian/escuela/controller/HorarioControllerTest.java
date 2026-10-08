package com.damian.escuela.controller;

import com.damian.escuela.dto.horarios.HorarioRequest;
import com.damian.escuela.dto.horarios.HorarioResponse;
import com.damian.escuela.dto.datos.DatosGrupo;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.services.horarios.HorarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HorarioController.class)
public class HorarioControllerTest {

    private static final String URL = "/api/horarios";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HorarioService horarioService;

    private HorarioRequest requestValido() {
        return new HorarioRequest(1L, "Lunes", "08:00", "10:00");
    }

    private HorarioResponse responseValida() {
        return new HorarioResponse(
                1L,
                new DatosGrupo("Matemáticas I", "Laura Martínez Martínez", "Aula 101", "2026-01"),
                "Lunes 08:00 - 10:00"
        );
    }

    // ---------- GET /api/horarios ----------

    @Test
    void listar_debeRetornar200ConLaLista() throws Exception {
        when(horarioService.listar()).thenReturn(List.of(responseValida()));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$[0].grupo.maestro").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$[0].grupo.aula").value("Aula 101"))
                .andExpect(jsonPath("$[0].grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$[0].horario").value("Lunes 08:00 - 10:00"));
    }

    @Test
    void listar_debeRetornar200ConListaVacia_cuandoNoHayHorarios() throws Exception {
        when(horarioService.listar()).thenReturn(List.of());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- GET /api/horarios/{id} ----------

    @Test
    void obtenerPorId_debeRetornar200_cuandoElHorarioExiste() throws Exception {
        when(horarioService.obtenerPorId(1L)).thenReturn(responseValida());

        mockMvc.perform(get(URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.grupo.maestro").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$.grupo.aula").value("Aula 101"))
                .andExpect(jsonPath("$.grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"));
    }

    @Test
    void obtenerPorId_debeRetornar404_cuandoElHorarioNoExiste() throws Exception {
        when(horarioService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Horario no encontrado con id: 99"));

        mockMvc.perform(get(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Horario no encontrado con id: 99"));
    }

    // ---------- POST /api/horarios ----------

    @Test
    void registrar_debeRetornar201_cuandoDatosSonValidos() throws Exception {
        when(horarioService.registrar(any(HorarioRequest.class))).thenReturn(responseValida());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.grupo.maestro").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$.grupo.aula").value("Aula 101"))
                .andExpect(jsonPath("$.grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"));

        verify(horarioService).registrar(any(HorarioRequest.class));
    }

    @Test
    void registrar_debeRetornar404_cuandoElGrupoNoExiste() throws Exception {
        when(horarioService.registrar(any(HorarioRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Grupo no encontrado con id: 99"));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Grupo no encontrado con id: 99"));
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeGrupoEsNulo() throws Exception {
        HorarioRequest request = new HorarioRequest(null, "Lunes", "08:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeGrupoEsCero() throws Exception {
        HorarioRequest request = new HorarioRequest(0L, "Lunes", "08:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeGrupoEsNegativo() throws Exception {
        HorarioRequest request = new HorarioRequest(-1L, "Lunes", "08:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElDiaEstaVacio() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "", "08:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElDiaSoloTieneEspacios() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "   ", "08:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElDiaEsNulo() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, null, "08:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElDiaEsMuyLargo() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "A".repeat(16), "08:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoLaHoraDeInicioEstaVacia() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "Lunes", "", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoLaHoraDeInicioEsNula() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "Lunes", null, "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoLaHoraDeInicioNoTieneCincoCaracteres() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "Lunes", "8:00", "10:00");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoLaHoraDeFinEstaVacia() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "Lunes", "08:00", "");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoLaHoraDeFinEsNula() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "Lunes", "08:00", null);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoLaHoraDeFinNoTieneCincoCaracteres() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "Lunes", "08:00", "10:0");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoNoHayBody() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).registrar(any());
    }

    // ---------- PUT /api/horarios/{id} ----------

    @Test
    void actualizar_debeRetornar200_cuandoDatosSonValidos() throws Exception {
        when(horarioService.actualizar(any(HorarioRequest.class), eq(1L))).thenReturn(responseValida());

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.grupo.maestro").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$.grupo.aula").value("Aula 101"))
                .andExpect(jsonPath("$.grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"));
    }

    @Test
    void actualizar_debeRetornar404_cuandoElHorarioNoExiste() throws Exception {
        when(horarioService.actualizar(any(HorarioRequest.class), eq(99L)))
                .thenThrow(new RecursoNoEncontradoException("Horario no encontrado con id: 99"));

        mockMvc.perform(put(URL + "/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Horario no encontrado con id: 99"));
    }

    @Test
    void actualizar_debeRetornar400_cuandoElBodyEsInvalido() throws Exception {
        HorarioRequest request = new HorarioRequest(1L, "", "08:00", "10:00");

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).actualizar(any(), any());
    }

    // ---------- DELETE /api/horarios/{id} ----------

    @Test
    void eliminar_debeRetornar204_cuandoElHorarioExiste() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(horarioService).eliminar(1L);
    }

    @Test
    void eliminar_debeRetornar404_cuandoElHorarioNoExiste() throws Exception {
        doThrow(new RecursoNoEncontradoException("Horario no encontrado con id: 99"))
                .when(horarioService).eliminar(99L);

        mockMvc.perform(delete(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Horario no encontrado con id: 99"));
    }
}
