package com.damian.escuela.enums;

import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DiaSemanaTest {

    @ParameterizedTest
    @CsvSource({
            "Lunes, LUNES",
            "Martes, MARTES",
            "Miércoles, MIERCOLES",
            "Jueves, JUEVES",
            "Viernes, VIERNES",
            "Sábado, SABADO"
    })
    void obtenerDiaSemanaPorDescripcion_debeRetornarEnum_cuandoDescripcionEsExacta(String descripcion, DiaSemama enumEsperado) {
        DiaSemama resultado = DiaSemama.obtenerDiaSemanaPorDescripcion(descripcion);
        assertThat(resultado).isEqualTo(enumEsperado);
    }

    @ParameterizedTest
    @CsvSource({
            "lunes, LUNES",
            "MIERCOLES, MIERCOLES",
            "miércoles, MIERCOLES",
            "miercoles, MIERCOLES",
            "sabado, SABADO",
            "SÁBADO, SABADO"
    })
    void obtenerDiaSemanaPorDescripcion_debeRetornarEnum_cuandoDescripcionTieneVariacionDeMayusculasOAcentos(String descripcion, DiaSemama enumEsperado) {
        DiaSemama resultado = DiaSemama.obtenerDiaSemanaPorDescripcion(descripcion);
        assertThat(resultado).isEqualTo(enumEsperado);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void obtenerDiaSemanaPorDescripcion_debeLanzarDatoInvalidoException_cuandoDescripcionEsNulaOVacia(String descripcionInvalida) {
        assertThatThrownBy(() -> DiaSemama.obtenerDiaSemanaPorDescripcion(descripcionInvalida))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La descripción es requerida");
    }

    @Test
    void obtenerDiaSemanaPorDescripcion_debeLanzarRecursoNoEncontradoException_cuandoDiaNoExiste() {
        String diaInexistente = "Domingo";

        assertThatThrownBy(() -> DiaSemama.obtenerDiaSemanaPorDescripcion(diaInexistente))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe un dia de la semana con la descripción: Domingo");
    }
}
