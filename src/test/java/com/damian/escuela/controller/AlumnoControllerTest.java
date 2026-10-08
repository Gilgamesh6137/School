package com.damian.escuela.controller;

import com.damian.escuela.dto.alumnos.AlumnoRequest;
import com.damian.escuela.dto.alumnos.AlumnoResponse;
import com.damian.escuela.dto.datos.DatosCalificacion;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.services.alumnos.AlumnoService;
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

@WebMvcTest(AlumnoController.class)
public class AlumnoControllerTest {

    private static final String URL = "/api/alumnos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlumnoService alumnoService;

    private AlumnoRequest requestValido() {
        return new AlumnoRequest("Carlos", "González", "Ramírez");
    }

    private AlumnoResponse responseValida() {
        return new AlumnoResponse(
                1L,
                "Carlos González Ramírez",
                "carlos.gonzalez@alumnos.com",
                "A2026001",
                "10/01/2026",
                List.of(new DatosCalificacion("Matemáticas I", "2026-1", new BigDecimal("8.5"))),
                new BigDecimal("8.5")
        );
    }

    // ---------- GET /api/alumnos ----------

    @Test
    void listar_debeRetornar200ConLaLista() throws Exception {
        when(alumnoService.listar()).thenReturn(List.of(responseValida()));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$[0].email").value("carlos.gonzalez@alumnos.com"))
                .andExpect(jsonPath("$[0].matricula").value("A2026001"))
                .andExpect(jsonPath("$[0].fechaIngreso").value("10/01/2026"))
                .andExpect(jsonPath("$[0].calificaciones.length()").value(1))
                .andExpect(jsonPath("$[0].calificaciones[0].curso").value("Matemáticas I"))
                .andExpect(jsonPath("$[0].calificaciones[0].calificacion").value(8.5))
                .andExpect(jsonPath("$[0].promedio").value(8.5));
    }

    @Test
    void listar_debeRetornar200ConListaVacia_cuandoNoHayAlumnos() throws Exception {
        when(alumnoService.listar()).thenReturn(List.of());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- GET /api/alumnos/{id} ----------

    @Test
    void obtenerPorId_debeRetornar200_cuandoElAlumnoExiste() throws Exception {
        when(alumnoService.obtenerPorId(1L)).thenReturn(responseValida());

        mockMvc.perform(get(URL + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$.email").value("carlos.gonzalez@alumnos.com"))
                .andExpect(jsonPath("$.matricula").value("A2026001"))
                .andExpect(jsonPath("$.fechaIngreso").value("10/01/2026"))
                .andExpect(jsonPath("$.calificaciones.length()").value(1))
                .andExpect(jsonPath("$.calificaciones[0].curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.calificaciones[0].calificacion").value(8.5))
                .andExpect(jsonPath("$.promedio").value(8.5));
    }

    @Test
    void obtenerPorId_debeRetornar404_cuandoElAlumnoNoExiste() throws Exception {
        when(alumnoService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado con id: 99"));

        mockMvc.perform(get(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Alumno no encontrado con id: 99"));
    }

    // ---------- POST /api/alumnos ----------

    @Test
    void registrar_debeRetornar201_cuandoDatosSonValidos() throws Exception {
        when(alumnoService.registrar(any(AlumnoRequest.class))).thenReturn(responseValida());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$.email").value("carlos.gonzalez@alumnos.com"))
                .andExpect(jsonPath("$.matricula").value("A2026001"))
                .andExpect(jsonPath("$.fechaIngreso").value("10/01/2026"))
                .andExpect(jsonPath("$.calificaciones.length()").value(1))
                .andExpect(jsonPath("$.calificaciones[0].curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.calificaciones[0].calificacion").value(8.5))
                .andExpect(jsonPath("$.promedio").value(8.5));

        verify(alumnoService).registrar(any(AlumnoRequest.class));
    }

    @Test
    void registrar_debeRetornar400_cuandoElNombreEstaVacio() throws Exception {
        AlumnoRequest request = new AlumnoRequest("", "González", "Ramírez");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElNombreEsMuyCorto() throws Exception {
        AlumnoRequest request = new AlumnoRequest("Ana", "González", "Ramírez");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElNombreEsMuyLargo() throws Exception {
        AlumnoRequest request = new AlumnoRequest("A".repeat(51), "González", "Ramírez");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElApellidoPaternoEstaVacio() throws Exception {
        AlumnoRequest request = new AlumnoRequest("Carlos", "", "Ramírez");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElApellidoPaternoEsMuyCorto() throws Exception {
        AlumnoRequest request = new AlumnoRequest("Carlos", "Paz", "Ramírez");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElApellidoMaternoEstaVacio() throws Exception {
        AlumnoRequest request = new AlumnoRequest("Carlos", "González", "");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElApellidoMaternoEsMuyCorto() throws Exception {
        AlumnoRequest request = new AlumnoRequest("Carlos", "González", "Gil");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoElNombreEsNulo() throws Exception {
        AlumnoRequest request = new AlumnoRequest(null, "González", "Ramírez");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    @Test
    void registrar_debeRetornar400_cuandoNoHayBody() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).registrar(any());
    }

    // ---------- PUT /api/alumnos/{id} ----------

    @Test
    void actualizar_debeRetornar200_cuandoDatosSonValidos() throws Exception {
        when(alumnoService.actualizar(any(AlumnoRequest.class), eq(1L))).thenReturn(responseValida());

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos González Ramírez"))
                .andExpect(jsonPath("$.email").value("carlos.gonzalez@alumnos.com"))
                .andExpect(jsonPath("$.matricula").value("A2026001"))
                .andExpect(jsonPath("$.fechaIngreso").value("10/01/2026"))
                .andExpect(jsonPath("$.calificaciones.length()").value(1))
                .andExpect(jsonPath("$.calificaciones[0].curso").value("Matemáticas I"))
                .andExpect(jsonPath("$.calificaciones[0].calificacion").value(8.5))
                .andExpect(jsonPath("$.promedio").value(8.5));
    }

    @Test
    void actualizar_debeRetornar404_cuandoElAlumnoNoExiste() throws Exception {
        when(alumnoService.actualizar(any(AlumnoRequest.class), eq(99L)))
                .thenThrow(new RecursoNoEncontradoException("Alumno no encontrado con id: 99"));

        mockMvc.perform(put(URL + "/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Alumno no encontrado con id: 99"));
    }

    @Test
    void actualizar_debeRetornar400_cuandoElBodyEsInvalido() throws Exception {
        AlumnoRequest request = new AlumnoRequest("", "González", "Ramírez");

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(alumnoService, never()).actualizar(any(), any());
    }

    // ---------- DELETE /api/alumnos/{id} ----------

    @Test
    void eliminar_debeRetornar204_cuandoElAlumnoExiste() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(alumnoService).eliminar(1L);
    }

    @Test
    void eliminar_debeRetornar404_cuandoElAlumnoNoExiste() throws Exception {
        doThrow(new RecursoNoEncontradoException("Alumno no encontrado con id: 99"))
                .when(alumnoService).eliminar(99L);

        mockMvc.perform(delete(URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Alumno no encontrado con id: 99"));
    }
}
