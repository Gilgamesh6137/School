package com.damian.escuela.dto.horarios;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar un horario")
public record HorarioRequest(

        @Schema(description = "ID del grupo", example = "1")
        @NotNull(message = "El ID del grupo es requerido")
        @Positive(message = "El ID del grupo debe ser positivo")
        Long idGrupo,

        @Schema(description = "Dia de la semana", example = "Lunes")
        @NotBlank(message = "El dia es requerido")
        @NotNull(message = "El dia es requerido")
        @Size( max = 15, message = "El dia debe tener 15 caracteres como máximo")
        String dia,

        @Schema(description = "Hora de inicio", example = "08:00")
        @NotBlank(message = "La hora de inicio es requerida")
        @NotNull(message = "La hora de inicio es requerida")
        @Size(min = 5, max = 5, message = "La hora de inicio debe tener exactamente 5 caracteres")
        @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "La hora de inicio debe tener un formato HH:mm")
        String horaInicio,

        @Schema(description = "Hora de fin", example = "18:00")
        @NotBlank(message = "La hora de fin es requerida")
        @NotNull(message = "La hora de fin es requerida")
        @Size(min = 5, max = 5, message = "La hora de fin debe tener exactamente 5 caracteres")
        @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$", message = "La hora de fin debe tener el formato HH:mm")
        String horaFin
) {}
