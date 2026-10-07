package com.damian.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un curso")
public record CursoRequest(

        @Schema(description = "Nombre del curso", example = "Matemáticas I")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 5, max = 100, message = "El nombre debe tener entre 5 y 100 caracteres")
        String nombre,

        @Schema(description = "Descripción del curso", example = "Fundamentos matemáticos para nivel básico")
        @NotBlank(message = "La descripción es requerida")
        @Size(min = 5, max = 200, message = "La descripción debe tener entre 5 y 200 caracteres")
        String descripcion,

        @Schema(description = "Créditos del curso", example = "9")
        @Positive(message = "Los créditos deben ser positivos")
        Integer creditos
) {}
