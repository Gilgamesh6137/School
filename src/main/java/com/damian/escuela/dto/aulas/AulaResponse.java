package com.damian.escuela.dto.aulas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un aula")
public record AulaResponse(

        @Schema(description = "ID del aula", example = "1")
        Long id,

        @Schema(description = "Nombre del aula", example = "Aula 101")
        String nombre,

        @Schema(description = "Capacidad del aula", example = "30")
        Integer capacidad
) {}
