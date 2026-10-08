package com.damian.escuela.controller;

import com.damian.escuela.dto.inscripciones.InscripcionRequest;
import com.damian.escuela.dto.inscripciones.InscripcionResponse;
import com.damian.escuela.dto.datos.DatosAlumno;
import com.damian.escuela.dto.datos.DatosGrupo;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.services.inscripciones.InscripcionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InscripcionController.class)
public class InscripcionControllerTest {

    private static final String URL = "/api/inscripciones";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InscripcionService inscripcionService;

    private InscripcionRequest requestValido() {
        return new InscripcionRequest(1L, 1L);
    }

    private InscripcionResponse responseValida() {
        return new InscripcionResponse(
                1L,
                new DatosAlumno("Carlos González Ramírez", "A2026001", "carlos.gonzalez@alumnos.com", "10/01/2026"),
                new DatosGrupo("Matemáticas I", "Laura Martínez Martínez", "Aula 101", "2026-01"),
                new BigDecimal("8.5"),
                "15/01/2026"
        );
    }

    // ---------- GET /api/inscripciones ----------

    @Test
    void listar_debeRetornar200ConLaLista() throws Exception {
        when(inscripcionService.listar()).thenReturn(List.of(responseValida()));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].alumno.nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$[0].alumno.matricula").value("A2026001"))
                .andExpect(jsonPath("$[0].grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$[0].grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$[0].calificacion").value(8.5))
                .andExpect(jsonPath("$[0].fechaInscripcion").value("15/01/2026"));
    }

    @Test
    void listar_debeRetornar200ConListaVacia_cuandoNoHayInscripciones() throws Exception {
        when(inscripcionService.listar()).thenReturn(List.of());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- GET /api/inscripciones/{id} ----------

    @Test
    void obtenerPorId_debeRetornar200_cuandoLaInscripcionExiste() throws Exception {
        when(inscripcionService.obtenerPorId(1L)).thenReturn(responseValida());

        mockMvc.perform(get(URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.alumno.nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$.alumno.matricula").value("A2026001"))
                .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$.calificacion").value(8.5))
                .andExpect(jsonPath("$.fechaInscripcion").value("15/01/2026"));
    }

    @Test
    void obtenerPorId_debeRetornar404_cuandoLaInscripcionNoExiste() throws Exception {
        when(inscripcionService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Inscripcion no encontrada con id: 99"));

        mockMvc.perform(get(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Inscripcion no encontrada con id: 99"));
    }

    // ---------- POST /api/inscripciones ----------

    @Test
    void registrar_debeRetornar201_cuandoDatosSonValidos() throws Exception {
        when(inscripcionService.registrar(any(InscripcionRequest.class))).thenReturn(responseValida());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.alumno.nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$.alumno.matricula").value("A2026001"))
                .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$.calificacion").value(8.5))
                .andExpect(jsonPath("$.fechaInscripcion").value("15/01/2026"));

        verify(inscripcionService).registrar(any(InscripcionRequest.class));
    }

    @Test
    void registrar_debeRetornar404_cuandoElAlumnoOElGrupoNoExiste() throws Exception {
        when(inscripcionService.registrar(any(InscripcionRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado con id: 99"));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Alumno no encontrado con id: 99"));
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeAlumnoEsNulo() throws Exception {
        InscripcionRequest request = new InscripcionRequest(null, 1L);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeAlumnoEsCero() throws Exception {
        InscripcionRequest request = new InscripcionRequest(0L, 1L);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeAlumnoEsNegativo() throws Exception {
        InscripcionRequest request = new InscripcionRequest(-1L, 1L);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeGrupoEsNulo() throws Exception {
        InscripcionRequest request = new InscripcionRequest(1L, null);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeGrupoEsCero() throws Exception {
        InscripcionRequest request = new InscripcionRequest(1L, 0L);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElIdDeGrupoEsNegativo() throws Exception {
        InscripcionRequest request = new InscripcionRequest(1L, -1L);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoNoHayBody() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).registrar(any());
    }

    // ---------- PUT /api/inscripciones/{id} ----------

    @Test
    void actualizar_debeRetornar200_cuandoDatosSonValidos() throws Exception {
        when(inscripcionService.actualizar(any(InscripcionRequest.class), eq(1L))).thenReturn(responseValida());

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.alumno.nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$.alumno.matricula").value("A2026001"))
                .andExpect(jsonPath("$.grupo.curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.grupo.periodo").value("2026-01"))
                .andExpect(jsonPath("$.calificacion").value(8.5))
                .andExpect(jsonPath("$.fechaInscripcion").value("15/01/2026"));
    }

    @Test
    void actualizar_debeRetornar404_cuandoLaInscripcionNoExiste() throws Exception {
        when(inscripcionService.actualizar(any(InscripcionRequest.class), eq(99L)))
                .thenThrow(new RecursoNoEncontradoException("Inscripcion no encontrada con id: 99"));

        mockMvc.perform(put(URL + "/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Inscripcion no encontrada con id: 99"));
    }

    @Test
    void actualizar_debeRetornar400_cuandoElBodyEsInvalido() throws Exception {
        InscripcionRequest request = new InscripcionRequest(null, 1L);

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(inscripcionService, never()).actualizar(any(), any());
    }

    // ---------- DELETE /api/inscripciones/{id} ----------

    @Test
    void eliminar_debeRetornar204_cuandoLaInscripcionExiste() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(inscripcionService).eliminar(1L);
    }

    @Test
    void eliminar_debeRetornar404_cuandoLaInscripcionNoExiste() throws Exception {
        doThrow(new RecursoNoEncontradoException("Inscripcion no encontrada con id: 99"))
                .when(inscripcionService).eliminar(99L);

        mockMvc.perform(delete(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Inscripcion no encontrada con id: 99"));
    }
}
