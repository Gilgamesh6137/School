package com.damian.escuela.dto.maestros;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un maestro")
public record MaestroRequest(

        @Schema(description = "Nombre del maestro", example = "Pedro")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del maestro", example = "Vásquez")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del maestro", example = "Rueda")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Email del maestro", example = "test@test.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 50, message = "El email debe tener entre 8 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @Schema(description = "Telefono del maestro", example = "1234567890")
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^[0-9]{10}", message = "El teléfono debe tener exactamente 10 caracteres")
        String telefono
) {
}
