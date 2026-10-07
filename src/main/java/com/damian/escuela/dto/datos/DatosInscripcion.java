package com.damian.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de una inscripcion de un curso")
public record DatosInscripcion(

        @Schema(description = "Datos del alumno", example = "Pedro Vásquez Rueda")
        DatosAlumno alumno,

        @Schema(description = "Datos del grupo", example = "Matemáticas I")
        DatosGrupo grupo,

        @Schema(description = "Fecha de inscripción del curso", example = "28/09/2026")
        String fechaInscripcion
) {}
