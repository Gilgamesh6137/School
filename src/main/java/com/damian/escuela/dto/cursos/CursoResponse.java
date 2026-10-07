package com.damian.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un curso")
public record CursoResponse(

        @Schema(description = "ID del curso", example = "1")
        Long id,

        @Schema(description = "Nombre del curso", example = "Matemáticas I")
        String nombre,

        @Schema(description = "Descripción del curso", example = "Fundamentos matemáticos para nivel básico")
        String descripcion,

        @Schema(description = "Créditos del curso", example = "9")
        Integer creditos
) {}
