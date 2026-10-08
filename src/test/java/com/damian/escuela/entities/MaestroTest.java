package com.damian.escuela.entities;

import com.damian.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaestroTest {

    private static final String MSG_NOMBRE = "El nombre es requerido y debe tener entre 1 y 50 caracteres";
    private static final String MSG_PATERNO = "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres";
    private static final String MSG_MATERNO = "El apellido materno es requerido y debe tener entre 1 y 50 caracteres";
    private static final String MSG_EMAIL = "El email es requerido y debe tener entre 5 y 100 caracteres";
    private static final String MSG_TEL_TAMANIO = "El teléfono es requerido y debe tener exactamente 10 caracteres";
    private static final String MSG_TEL_INVALIDO = "El teléfono es invalido";

    private static Maestro maestroBase() {
        return Maestro.crear("Laura", "Martínez", "Martínez",
                "laura.martinez@escuela.com", "5551010789");
    }

    // ---------- crear ----------

    @Test
    void crear_debeCrearMaestro_cuandoDatosSonValidos() {
        Maestro maestro = Maestro.crear(
                "Laura", "Martínez", "Sánchez",
                "laura.martinez@escuela.com", "5551010789");

        assertThat(maestro.getNombre()).isEqualTo("Laura");
        assertThat(maestro.getApellidoPaterno()).isEqualTo("Martínez");
        assertThat(maestro.getApellidoMaterno()).isEqualTo("Sánchez");
        assertThat(maestro.getEmail()).isEqualTo("laura.martinez@escuela.com");
        assertThat(maestro.getTelefono()).isEqualTo("5551010789");
        assertThat(maestro.getGrupos()).isEmpty();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Ana|Martínez|Martínez|a@escuela.com|5551010789|" + MSG_NOMBRE,
            "Laura|Mart|Martínez|a@escuela.com|5551010789|" + MSG_PATERNO,
            "Laura|Martínez|Mart|a@escuela.com|5551010789|" + MSG_MATERNO,
            "Laura|Martínez|Martínez|a@b|5551010789|" + MSG_EMAIL,
            "|Martínez|Martínez|a@escuela.com|5551010789|" + MSG_NOMBRE,
            "Laura||Martínez|a@escuela.com|5551010789|" + MSG_PATERNO,
            "Laura|Martínez||a@escuela.com|5551010789|" + MSG_MATERNO,
            "Laura|Martínez|Martínez||5551010789|" + MSG_EMAIL,
            "Laura|Martínez|Martínez|a@escuela.com||" + MSG_TEL_TAMANIO
    })
    void crear_debeLanzarExcepcion_cuandoUnCampoEsInvalido(
            String nombre, String paterno, String materno,
            String email, String telefono, String mensaje) {

        assertThatThrownBy(() -> Maestro.crear(nombre, paterno, materno, email, telefono))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(mensaje);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoElNombreEsMuyLargo() {
        assertThatThrownBy(() -> Maestro.crear("a".repeat(51), "Martínez", "Martínez",
                "a@escuela.com", "5551010789"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_NOMBRE);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoElEmailEsMuyLargo() {
        String email = "a".repeat(101);

        assertThatThrownBy(() -> Maestro.crear("Laura", "Martínez", "Martínez", email, "5551010789"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_EMAIL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"555101078", "55510107890", "123"})
    void crear_debeLanzarExcepcion_cuandoElTelefonoNoTiene10Caracteres(String telefono) {
        assertThatThrownBy(() -> Maestro.crear("Laura", "Martínez", "Martínez",
                "a@escuela.com", telefono))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_TEL_TAMANIO);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"     "})
    void crear_debeLanzarExcepcion_cuandoElEmailEsNuloOVacio(String email) {
        assertThatThrownBy(() -> Maestro.crear("Laura", "Martínez", "Martínez", email, "5551010789"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_EMAIL);
    }

    // ---------- actualizar ----------

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        Maestro maestro = maestroBase();

        maestro.actualizar("Carlos", "Hernández", "Ramírez",
                "carlos@escuela.com", "5552020359");

        assertThat(maestro.getNombre()).isEqualTo("Carlos");
        assertThat(maestro.getApellidoPaterno()).isEqualTo("Hernández");
        assertThat(maestro.getApellidoMaterno()).isEqualTo("Ramírez");
        assertThat(maestro.getEmail()).isEqualTo("carlos@escuela.com");
        assertThat(maestro.getTelefono()).isEqualTo("5552020359");
    }

    @Test
    void actualizar_noDebeCambiarNada_cuandoElTelefonoEsInvalido() {
        Maestro maestro = maestroBase();

        assertThatThrownBy(() -> maestro.actualizar("Carlos", "Hernández", "Ramírez",
                "carlos@escuela.com", "12345"))
                .isInstanceOf(DatoInvalidoException.class);

        assertThat(maestro.getNombre()).isEqualTo("Laura");
        assertThat(maestro.getEmail()).isEqualTo("laura.martinez@escuela.com");
        assertThat(maestro.getTelefono()).isEqualTo("5551010789");
    }
}
