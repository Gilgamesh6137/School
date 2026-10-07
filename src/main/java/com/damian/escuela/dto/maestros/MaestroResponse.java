package com.damian.escuela.dto.maestros;

import com.damian.escuela.dto.datos.DatosCurso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Información de un maestro")
public record MaestroResponse(

        @Schema(description = "ID del maestro", example = "1")
        Long id,

        @Schema(description = "Nombre completo del maestro", example = "Máximo Décimo Meridio")
        String nombre,

        @Schema(description = "Email del maestro", example = "test@test.com")
        String email,

        @Schema(description = "Teléfono del maestro", example = "1234567890")
        String telefono,

        @Schema(description = "Datos de los cursos del maestro")
        List<DatosCurso> cursos
) {}
