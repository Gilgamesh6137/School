package com.damian.escuela.dto.calificacion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para registrar o actualizar una calificación")
public record CalificacionRequest(

        @Schema(description = "ID de la calificación", example = "1")
        @NotNull(message = "El ID es requerido")
        @Positive(message = "El ID debe ser positivo")
        Long idInscripcion,

        @Schema(description = "Calificación del curso", example = "8.0")
        @NotNull(message = "La calificación es requerida")
        @Positive(message = "La calificación debe ser positiva")
        BigDecimal calificacion
) {}
