package com.damian.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Datos de una calificación")
public record DatosCalificacion(

        @Schema(description = "Nombre del curso", example = "Matemáticas I")
        String curso,

        @Schema(description = "Periodo de la calificación", example = "2026-09")
        String periodo,

        @Schema(description = "Valor de la calificación", example = "9.9")
        BigDecimal calificacion
) {}
