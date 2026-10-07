package com.damian.escuela.dto.alumnos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un alumno")
public record AlumnoRequest(

        @Schema(description = "Nombre del alumno", example = "Máximo")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 5, max = 50, message = "El nombre debe tener entre 5 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del alumno", example = "Décimo")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 5, max = 50, message = "El apellido paterno debe tener entre 5 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del alumno", example = "Meridio")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 5, max = 50, message = "El apellido materno debe tener entre 5 y 50 caracteres")
        String apellidoMaterno
) {}
