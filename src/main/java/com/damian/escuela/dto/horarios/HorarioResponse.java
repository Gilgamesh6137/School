package com.damian.escuela.dto.horarios;

import com.damian.escuela.dto.datos.DatosGrupo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un horario")
public record HorarioResponse(

        @Schema(description = "ID del horario", example = "1")
        Long id,

        @Schema(description = "Datos del grupo")
        DatosGrupo grupo,

        @Schema(description = "Horario del grupo", example = "Lunes 8:00 - 18:00")
        String horario
) {}
