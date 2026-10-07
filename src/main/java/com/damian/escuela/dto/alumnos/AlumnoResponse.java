package com.damian.escuela.dto.alumnos;

import com.damian.escuela.dto.datos.DatosCalificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Información de un alumno")
public record AlumnoResponse(
        @Schema(description = "ID del alumno", example = "1")
        Long id,

        @Schema(description = "Nombre completo del alumno", example = "Máximo Décimo Meridio")
        String nombre,

        @Schema(description = "Email del alumno", example = "test@test.com")
        String email,

        @Schema(description = "Matricula del alumno", example = "1234567890")
        String matricula,

        @Schema(description = "Fecha de ingreso del alumno", example = "12/09/2026")
        String fechaIngreso,

        @Schema(description = "Datos de las calificaciones del alumno")
        List<DatosCalificacion> calificaciones,

        @Schema(description = "Promedio del alumno", example = "9.0")
        BigDecimal promedio
) {}
