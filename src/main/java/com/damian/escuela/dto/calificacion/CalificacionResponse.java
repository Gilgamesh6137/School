package com.damian.escuela.dto.calificacion;

import com.damian.escuela.dto.datos.DatosInscripcion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Datos de la calificación de un alumno")
public record CalificacionResponse(

        @Schema(description = "ID de la calificación", example = "1")
        Long id,

        @Schema(description = "Datos de la inscripción del curso")
        DatosInscripcion inscripcion,

        @Schema(description = "Calificación del curso", example = "8")
        BigDecimal calificacion,

        @Schema(description = "Fecha de registro del curso", example = "11/02/2026")
        String fechaRegistro
) {}
