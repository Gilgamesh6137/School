package com.damian.escuela.dto.grupos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para registrar o actualizar un grupo")
public record GrupoRequest(

        @Schema(description = "ID del curso", example = "1")
        @NotNull(message = "El ID del curso es requerido")
        @Positive(message = "El ID del curso debe ser positivo")
        Long idCurso,

        @Schema(description = "ID del maestro", example = "1")
        @NotNull(message = "El ID del maestro es requerido")
        @Positive(message = "El ID del maestro debe ser positivo")
        Long idMaestro,

        @Schema(description = "ID del aula", example = "1")
        @NotNull(message = "El ID del aula es requerido")
        @Positive(message = "El ID del aula debe ser positivo")
        Long idAula,

        @Schema(description = "Periodo del grupo", example = "2025-01")
        @NotBlank(message = "El periodo es requerido")
        @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "El periodo debe tener el formato YYYY-MM")
        @Size(min = 6, max = 20, message = "El periodo debe tener entre 6 y 20 caracteres")
        String periodo
) {}
