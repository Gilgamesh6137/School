package com.damian.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un grupo")
public record DatosGrupo(

        @Schema(description = "Nombre del curso", example = "Matemáticas I")
        String curso,

        @Schema(description = "Nombre del maestro", example = "Máximo Décimo Meridio")
        String maestro,

        @Schema(description = "Nombre del aula", example = "Aula 101")
        String aula,

        @Schema(description = "Periodo del grupo", example = "2026-9")
        String periodo
) {}
