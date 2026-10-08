package com.damian.escuela.controller;

import com.damian.escuela.dto.grupos.GrupoRequest;
import com.damian.escuela.dto.grupos.GrupoResponse;
import com.damian.escuela.dto.datos.DatosAula;
import com.damian.escuela.dto.datos.DatosCurso;
import com.damian.escuela.dto.datos.DatosMaestro;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.services.grupos.GrupoService;
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

@WebMvcTest(GrupoController.class)
public class GrupoControllerTest {

    private static final String URL = "/api/grupos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GrupoService grupoService;

    private GrupoRequest requestValido() {
        return new GrupoRequest(1L, 1L, 1L, "2026-01");
    }

    private GrupoResponse responseValida() {
        return new GrupoResponse(
                1L,
                new DatosCurso("Matemáticas I", "Fundamentos matemáticos para nivel básico", 6),
                new DatosMaestro("Laura Martínez Martínez", "laura.martinez@escuela.com", "5551010789"),
                new DatosAula("Aula 101", 30),
                List.of("Lunes 10:00 - 12:00", "Miércoles 14:00 - 16:00"),
                "2026-01"
        );
    }

    // ---------- GET /api/grupos ----------

    @Test
    void listar_debeRetornar200ConLaLista() throws Exception {
        when(grupoService.listar()).thenReturn(List.of(responseValida()));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].curso.nombre").value("Matemáticas I"))
                .andExpect(jsonPath("$[0].curso.creditos").value(6))
                .andExpect(jsonPath("$[0].maestro.nombre").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$[0].maestro.email").value("laura.martinez@escuela.com"))
                .andExpect(jsonPath("$[0].aula.nombre").value("Aula 101"))
                .andExpect(jsonPath("$[0].aula.capacidad").value(30))
                .andExpect(jsonPath("$[0].horarios.length()").value(2))
                .andExpect(jsonPath("$[0].horarios[0]").value("Lunes 10:00 - 12:00"))
                .andExpect(jsonPath("$[0].periodo").value("2026-01"));
    }

    @Test
    void listar_debeRetornar200ConListaVacia_cuandoNoHayGrupos() throws Exception {
        when(grupoService.listar()).thenReturn(List.of());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- GET /api/grupos/{id} ----------

    @Test
    void obtenerPorId_debeRetornar200_cuandoElGrupoExiste() throws Exception {
        when(grupoService.obtenerPorId(1L)).thenReturn(responseValida());

        mockMvc.perform(get(URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.curso.nombre").value("Matemáticas I"))
                .andExpect(jsonPath("$.curso.creditos").value(6))
                .andExpect(jsonPath("$.maestro.nombre").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$.maestro.email").value("laura.martinez@escuela.com"))
                .andExpect(jsonPath("$.aula.nombre").value("Aula 101"))
                .andExpect(jsonPath("$.aula.capacidad").value(30))
                .andExpect(jsonPath("$.horarios.length()").value(2))
                .andExpect(jsonPath("$.horarios[0]").value("Lunes 10:00 - 12:00"))
                .andExpect(jsonPath("$.periodo").value("2026-01"));
    }

    @Test
    void obtenerPorId_debeRetornar404_cuandoElGrupoNoExiste() throws Exception {
        when(grupoService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Grupo no encontrado con id: 99"));

        mockMvc.perform(get(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Grupo no encontrado con id: 99"));
    }

    // ---------- POST /api/grupos ----------

    @Test
    void registrar_debeRetornar201_cuandoDatosSonValidos() throws Exception {
        when(grupoService.registrar(any(GrupoRequest.class))).thenReturn(responseValida());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.curso.nombre").value("Matemáticas I"))
                .andExpect(jsonPath("$.curso.creditos").value(6))
                .andExpect(jsonPath("$.maestro.nombre").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$.maestro.email").value("laura.martinez@escuela.com"))
                .andExpect(jsonPath("$.aula.nombre").value("Aula 101"))
                .andExpect(jsonPath("$.aula.capacidad").value(30))
                .andExpect(jsonPath("$.horarios.length()").value(2))
                .andExpect(jsonPath("$.horarios[0]").value("Lunes 10:00 - 12:00"))
                .andExpect(jsonPath("$.periodo").value("2026-01"));

        verify(grupoService).registrar(any(GrupoRequest.class));
    }

    @Test
    void registrar_debeRetornar404_cuandoElCursoElMaestroOElAulaNoExiste() throws Exception {
        when(grupoService.registrar(any(GrupoRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Curso no encontrado con id: 99"));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Curso no encontrado con id: 99"));
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeCursoEsNulo() throws Exception {
        GrupoRequest request = new GrupoRequest(null, 1L, 1L, "2026-01");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeCursoEsCero() throws Exception {
        GrupoRequest request = new GrupoRequest(0L, 1L, 1L, "2026-01");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeMaestroEsNulo() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, null, 1L, "2026-01");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeMaestroEsNegativo() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, -1L, 1L, "2026-01");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeAulaEsNulo() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, 1L, null, "2026-01");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeAulaEsCero() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, 1L, 0L, "2026-01");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElPeriodoEstaVacio() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, 1L, 1L, "");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElPeriodoEsNulo() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, 1L, 1L, null);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElPeriodoTieneFormatoInvalido() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, 1L, 1L, "2026/01");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElPeriodoTieneUnSoloDigitoDeMes() throws Exception {
        GrupoRequest request = new GrupoRequest(1L, 1L, 1L, "2026-1");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoNoHayBody() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).registrar(any());
    }

    // ---------- PUT /api/grupos/{id} ----------

    @Test
    void actualizar_debeRetornar200_cuandoDatosSonValidos() throws Exception {
        when(grupoService.actualizar(any(GrupoRequest.class), eq(1L))).thenReturn(responseValida());

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.curso.nombre").value("Matemáticas I"))
                .andExpect(jsonPath("$.curso.creditos").value(6))
                .andExpect(jsonPath("$.maestro.nombre").value("Laura Martínez Martínez"))
                .andExpect(jsonPath("$.maestro.email").value("laura.martinez@escuela.com"))
                .andExpect(jsonPath("$.aula.nombre").value("Aula 101"))
                .andExpect(jsonPath("$.aula.capacidad").value(30))
                .andExpect(jsonPath("$.horarios.length()").value(2))
                .andExpect(jsonPath("$.horarios[0]").value("Lunes 10:00 - 12:00"))
                .andExpect(jsonPath("$.periodo").value("2026-01"));
    }

    @Test
    void actualizar_debeRetornar404_cuandoElGrupoNoExiste() throws Exception {
        when(grupoService.actualizar(any(GrupoRequest.class), eq(99L)))
                .thenThrow(new RecursoNoEncontradoException("Grupo no encontrado con id: 99"));

        mockMvc.perform(put(URL + "/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Grupo no encontrado con id: 99"));
    }

    @Test
    void actualizar_debeRetornar400_cuandoElBodyEsInvalido() throws Exception {
        GrupoRequest request = new GrupoRequest(null, 1L, 1L, "2026-01");

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(grupoService, never()).actualizar(any(), any());
    }

    // ---------- DELETE /api/grupos/{id} ----------

    @Test
    void eliminar_debeRetornar204_cuandoElGrupoExiste() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(grupoService).eliminar(1L);
    }

    @Test
    void eliminar_debeRetornar404_cuandoElGrupoNoExiste() throws Exception {
        doThrow(new RecursoNoEncontradoException("Grupo no encontrado con id: 99"))
                .when(grupoService).eliminar(99L);

        mockMvc.perform(delete(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Grupo no encontrado con id: 99"));
    }
}
