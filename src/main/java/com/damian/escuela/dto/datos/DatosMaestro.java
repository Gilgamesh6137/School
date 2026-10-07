package com.damian.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un maestro")
public record DatosMaestro(

        @Schema(description = "Nombre del maestro", example = "Constantine Kovar Stein")
        String nombre,

        @Schema(description = "Email del maestro", example = "test@test.com")
        String email,

        @Schema(description = "Numero de teléfono del maestro", example = "1234567890")
        String telefono
) {}
