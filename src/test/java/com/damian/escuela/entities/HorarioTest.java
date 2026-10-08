package com.damian.escuela.entities;

import com.damian.escuela.enums.DiaSemama;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.utils.DateCustomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HorarioTest {

    private static final String MSG_INICIO_REQUERIDO =
            "La hora de inicio es requerida";

    private static final String MSG_INICIO_FORMATO =
            "El formato de hora inicio debe ser HH:mm";

    private static final String MSG_FIN_REQUERIDO =
            "La hora de fin es requerida";

    private static final String MSG_FIN_FORMATO =
            "El formato de la hora fin debe ser HH:mm";

    private static final String MSG_ORDEN =
            "La hora de inicio debe ser menor a la hora de fin";

    private final Grupo grupo = Grupo.crear("2026-01");

    private Horario horarioBase() {
        Horario horario = Horario.crear(DiaSemama.LUNES.getDescripcion(), "08:00", "10:00");
        horario.asignarGrupo(grupo);

        return horario;
    }

    // ---------- crear ----------

    @Test
    void crear_debeCrearHorario_cuandoDatosSonValidos() {
        Horario horario = Horario.crear(DiaSemama.LUNES.getDescripcion(), "08:00", "10:00");
        assertThat(horario.getHoraInicio()).isEqualTo("08:00");
        assertThat(horario.getHoraFin()).isEqualTo("10:00");
        assertThat(horario.getGrupo()).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "00:00, 00:01",
            "07:30, 09:15",
            "22:00, 23:59"
    })
    void crear_debeAceptarHorasValidas(String inicio, String fin) {
        Horario horario = Horario.crear(DiaSemama.LUNES.getDescripcion(), inicio, fin);
        assertThat(horario.getHoraInicio()).isEqualTo(inicio);
        assertThat(horario.getHoraFin()).isEqualTo(fin);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void crear_debeLanzarExcepcion_cuandoLaHoraInicioEsNulaOVacia(String hora) {
        assertThatThrownBy(() ->
                Horario.crear(DiaSemama.LUNES.getDescripcion(), hora, "10:00")
        ).isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_INICIO_REQUERIDO);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void crear_debeLanzarExcepcion_cuandoLaHoraFinEsNulaOVacia(String hora) {
        assertThatThrownBy(() ->
                Horario.crear(DiaSemama.LUNES.getDescripcion(), "08:00", hora)
        ).isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_FIN_REQUERIDO);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "8:00",
            "0800",
            "25:00",
            "08:60",
            "ab:cd",
            "08-00",
            "08:0"
    })
    void crear_debeLanzarExcepcion_cuandoElFormatoDeHoraInicioEsInvalido(String hora) {
        assertThatThrownBy(() ->
                Horario.crear(DiaSemama.LUNES.getDescripcion(), hora, "23:00")
        ).isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_INICIO_FORMATO);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "9:00",
            "1000",
            "25:00",
            "10:60",
            "ab:cd",
            "10-00",
            "10:0"
    })
    void crear_debeLanzarExcepcion_cuandoElFormatoDeHoraFinEsInvalido(String hora) {
        assertThatThrownBy(() ->
                Horario.crear(DiaSemama.LUNES.getDescripcion(), "08:00", hora)
        ).isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_FIN_FORMATO);
    }

    @ParameterizedTest
    @CsvSource({
            "10:00, 10:00",
            "10:00, 08:00",
            "23:59, 00:00",
            "09:01, 09:00"
    })
    void crear_debeLanzarExcepcion_cuandoLaHoraFinNoEsPosteriorALaDeInicio(String inicio, String fin) {
        assertThatThrownBy(() -> DateCustomUtils.compararHoras(inicio, fin, MSG_ORDEN))
                .isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_ORDEN);
    }

    // ---------- obtenerHorarioCompleto ----------

    @Test
    void obtenerHorarioCompleto_debeMostrarElDiaYLasHoras() {
        Horario horario = horarioBase();
        assertThat(horario.obtenerHorarioCompleto()).isEqualTo("Lunes 08:00 - 10:00");
    }

    // ---------- asignarGrupo ----------

    @Test
    void asignarGrupo_debeAsignarElGrupo_cuandoNoEsNulo() {
        Horario horario = Horario.crear(DiaSemama.LUNES.getDescripcion(), "08:00", "10:00");
        horario.asignarGrupo(grupo);
        assertThat(horario.getGrupo()).isSameAs(grupo);
    }

    @Test
    void asignarGrupo_debeLanzarExcepcion_cuandoEsNulo() {
        Horario horario = Horario.crear(DiaSemama.LUNES.getDescripcion(), "08:00", "10:00");
        assertThatThrownBy(() ->
                horario.asignarGrupo(null)
        ).isInstanceOf(DatoInvalidoException.class).hasMessage("El grupo no puede ser nulo");
    }

    // ---------- cambioEnDatos ----------

    @Test
    void cambioEnDatos_debeRetornarFalse_cuandoTodoEsIgual() {
        Horario horario = horarioBase();

        assertThat(
                horario.cambioEnDatos(
                        DiaSemama.LUNES.getDescripcion(),
                        "08:00",
                        "10:00",
                        grupo
                )
        ).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "MARTES, 08:00, 10:00",
            "LUNES, 09:00, 10:00",
            "LUNES, 08:00, 11:00"
    })
    void cambioEnDatos_debeRetornarTrue_cuandoCambiaDiaOHoras(String dia, String inicio, String fin) {
        Horario horario = horarioBase();
        assertThat(horario.cambioEnDatos(dia, inicio, fin, grupo)).isTrue();
    }

    @Test
    void cambioEnDatos_debeRetornarTrue_cuandoCambiaElGrupo() {
        Horario horario = horarioBase();
        Grupo otroGrupo = Grupo.crear("2026-02");

        assertThat(
                horario.cambioEnDatos(
                        DiaSemama.LUNES.getDescripcion(),
                        "08:00",
                        "10:00",
                        otroGrupo
                )
        ).isTrue();
    }

    @Test
    void cambioEnDatos_debeLanzarExcepcion_cuandoElGrupoEsNulo() {
        Horario horario = horarioBase();

        assertThatThrownBy(() ->
                horario.cambioEnDatos(
                        DiaSemama.LUNES.getDescripcion(),
                        "08:00",
                        "10:00",
                        null
                )
        ).isInstanceOf(DatoInvalidoException.class).hasMessage("El grupo es requerido");
    }

    // ---------- actualizar ----------

    @Test
    void actualizar_debeModificarTodosLosCampos() {
        Horario horario = horarioBase();
        Grupo otroGrupo = Grupo.crear("2026-02");

        horario.actualizar(
                DiaSemama.VIERNES.getDescripcion(),
                "12:00",
                "14:00",
                otroGrupo
        );

        assertThat(horario.getDiaSemama()).isEqualTo(DiaSemama.VIERNES);
        assertThat(horario.getHoraInicio()).isEqualTo("12:00");
        assertThat(horario.getHoraFin()).isEqualTo("14:00");
        assertThat(horario.getGrupo()).isSameAs(otroGrupo);
    }
}
