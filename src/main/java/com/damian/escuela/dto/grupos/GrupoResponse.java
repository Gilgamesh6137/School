package com.damian.escuela.dto.grupos;

import com.damian.escuela.dto.datos.DatosAula;
import com.damian.escuela.dto.datos.DatosCurso;
import com.damian.escuela.dto.datos.DatosMaestro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Datos de un grupo")
public record GrupoResponse(

        @Schema(description = "ID del grupo", example = "1")
        Long id,

        @Schema(description = "Datos del curso")
        DatosCurso curso,

        @Schema(description = "Datos del maestro")
        DatosMaestro maestro,

        @Schema(description = "Datos del aula")
        DatosAula aula,

        @Schema(description = "Horarios del grupo",
                example = "[\"Lunes 08:00 - 18:00\", \"Sábado 08:00 - 13:00\"]")
        List<String> horarios,

        @Schema(description = "Fecha de ingreso del alumno", example = "28/09/2026")
        String periodo
) {}
