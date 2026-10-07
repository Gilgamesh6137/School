package com.damian.escuela.dto.inscripciones;

import com.damian.escuela.dto.datos.DatosAlumno;
import com.damian.escuela.dto.datos.DatosCalificacion;
import com.damian.escuela.dto.datos.DatosGrupo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Información de una inscripción")
public record InscripcionResponse(

        @Schema(description = "ID de la inscripción", example = "1")
        Long id,

        @Schema(description = "Datos del alumno inscrito")
        DatosAlumno alumno,

        @Schema(description = "Datos del grupo inscrito")
        DatosGrupo grupo,

        @Schema(description = "Calificación del alumno", example = "9.0")
        BigDecimal calificacion,

        @Schema(description = "Fecha de inscripción", example = "11/02/2026")
        String fechaInscripcion
) {}
