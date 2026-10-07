package com.damian.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un alumno")
public record DatosAlumno(
        @Schema(description = "Nombre del alumno", example = "Damián")
        String nombre,

        @Schema(description = "Matricula del alumno", example = "VARUDA2602")
        String matricula,

        @Schema(description = "Email del alumno", example = "damian@tr.com")
        String email,

        @Schema(description = "Fecha de ingreso del alumno", example = "26/09/2026")
        String fechaIngreso
) {}
