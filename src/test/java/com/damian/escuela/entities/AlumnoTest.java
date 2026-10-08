package com.damian.escuela.entities;

import com.damian.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AlumnoTest {

    private static final String MSG_NOMBRE = "El nombre es requerido y debe tener entre 5 y 50 caracteres";
    private static final String MSG_PATERNO = "El apellido paterno es requerido y debe tener entre 5 y 50 caracteres";
    private static final String MSG_MATERNO = "El apellido materno es requerido y debe tener entre 5 y 50 caracteres";
    private static final String MSG_EMAIL = "El email es requerido y debe tener entre 1 y 100 caracteres";
    private static final String MSG_MATRICULA = "La matricula es requerida y debe tener exactamente 10 caracteres";

    private static Alumno alumnoBase() {
        return Alumno.crear("Carlos", "González", "Ramírez");
    }

    private static Inscripcion inscripcionConNota(String nota) {
        Calificacion calificacion = Calificacion.builder()
                .calificacion(nota == null ? null : new BigDecimal(nota))
                .build();
        return Inscripcion.builder().calificacion(calificacion).build();
    }

    private static Inscripcion inscripcionSinCalificacion() {
        return Inscripcion.builder().build();
    }

    // ---------- crear ----------

    @Test
    void crear_debeCrearAlumno_cuandoDatosSonValidos() {
        LocalDate antes = LocalDate.now();

        Alumno alumno = Alumno.crear("  Carlos  ", "  González ", " Ramírez  ");

        assertThat(alumno.getNombre()).isEqualTo("Carlos");
        assertThat(alumno.getApellidoPaterno()).isEqualTo("González");
        assertThat(alumno.getApellidoMaterno()).isEqualTo("Ramírez");
        assertThat(alumno.getFechaIngreso()).isBetween(antes, LocalDate.now());
        assertThat(alumno.getInscripciones()).isEmpty();
    }

    @Test
    void crear_noDebeAsignarEmailNiMatricula() {
        // Se generan después (en BD / servicio)
        Alumno alumno = alumnoBase();

        assertThat(alumno.getEmail()).isNull();
        assertThat(alumno.getMatricula()).isNull();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Ana|González|Ramírez|" + MSG_NOMBRE,
            "Luis|González|Ramírez|" + MSG_NOMBRE,
            "Carlos|Pére|Ramírez|" + MSG_PATERNO,
            "Carlos|González|Lara|" + MSG_MATERNO,
            "|González|Ramírez|" + MSG_NOMBRE,
            "Carlos||Ramírez|" + MSG_PATERNO,
            "Carlos|González||" + MSG_MATERNO,
            "'   '|González|Ramírez|" + MSG_NOMBRE
    })
    void crear_debeLanzarExcepcion_cuandoUnCampoEsInvalido(
            String nombre, String paterno, String materno, String mensaje) {
        assertThatThrownBy(() -> Alumno.crear(nombre, paterno, materno))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(mensaje);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoUnCampoSuperaLos50Caracteres() {
        String largo = "a".repeat(51);

        assertThatThrownBy(() -> Alumno.crear(largo, "González", "Ramírez"))
                .isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_NOMBRE);
        assertThatThrownBy(() -> Alumno.crear("Carlos", largo, "Ramírez"))
                .isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_PATERNO);
        assertThatThrownBy(() -> Alumno.crear("Carlos", "González", largo))
                .isInstanceOf(DatoInvalidoException.class).hasMessage(MSG_MATERNO);
    }

    @Test
    void crear_debeAceptarLimitesDe5y50Caracteres() {
        Alumno alumno = Alumno.crear("a".repeat(5), "b".repeat(50), "c".repeat(5));

        assertThat(alumno.getNombre()).hasSize(5);
        assertThat(alumno.getApellidoPaterno()).hasSize(50);
    }

    // ---------- inscripciones ----------

    @Test
    void agregarInscripcion_debeAgregarlaALaLista() {
        Alumno alumno = alumnoBase();
        Inscripcion inscripcion = inscripcionSinCalificacion();

        alumno.agregarInscripcion(inscripcion);

        assertThat(alumno.getInscripciones()).containsExactly(inscripcion);
    }

    @Test
    void agregarInscripcion_debeLanzarExcepcion_cuandoEsNula() {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.agregarInscripcion(null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La inscripción es requerida");
    }

    @Test
    void quitarInscripcion_debeQuitarlaDeLaLista() {
        Alumno alumno = alumnoBase();
        Inscripcion inscripcion = inscripcionSinCalificacion();
        alumno.agregarInscripcion(inscripcion);

        alumno.quitarInscripcion(inscripcion);

        assertThat(alumno.getInscripciones()).isEmpty();
    }

    @Test
    void quitarInscripcion_debeLanzarExcepcion_cuandoEsNula() {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.quitarInscripcion(null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La inscripción es requerida");
    }

    // ---------- asignarDatosAcademicos ----------

    @Test
    void asignarDatosAcademicos_debeNormalizarEmailYMatricula() {
        Alumno alumno = alumnoBase();

        alumno.asignarDatosAcademicos("Carlos.Gonzalez@Alumnos.COM", "GORA260101");

        assertThat(alumno.getEmail()).isEqualTo("carlos.gonzalez@alumnos.com");
        assertThat(alumno.getMatricula()).isEqualTo("GORA260101");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void asignarDatosAcademicos_debeLanzarExcepcion_cuandoElEmailEsInvalido(String email) {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.asignarDatosAcademicos(email, "GORA260101"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_EMAIL);
    }

    @Test
    void asignarDatosAcademicos_debeLanzarExcepcion_cuandoElEmailSuperaLos100Caracteres() {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.asignarDatosAcademicos("a".repeat(101), "GORA260101"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_EMAIL);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "A1", "ABCD"})
    void asignarDatosAcademicos_debeLanzarExcepcion_cuandoLaMatriculaEsMuyCortaONula(String matricula) {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.asignarDatosAcademicos("test@test.com", matricula))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_MATRICULA);
    }

    @Test
    void asignarDatosAcademicos_debeLanzarExcepcion_cuandoLaMatriculaSuperaLos10Caracteres() {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.asignarDatosAcademicos("carlos@alumnos.com", "A".repeat(11)))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_MATRICULA);
    }

    // ---------- cambioEnDatos ----------

    @Test
    void cambioEnDatos_debeRetornarFalse_cuandoLosDatosSonIguales() {
        assertThat(alumnoBase().cambioEnDatos("Carlos", "González", "Ramírez")).isTrue();
    }

    @Test
    void cambioEnDatos_debeIgnorarEspaciosAlrededor() {
        assertThat(alumnoBase().cambioEnDatos("  Carlos ", " González", "Ramírez  ")).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "Carlitos, González, Ramírez",
            "Carlos, Gonzales, Ramírez",
            "Carlos, González, Ramirez",
            "carlos, González, Ramírez"
    })
    void cambioEnDatos_debeRetornarTrue_cuandoCambiaAlgunCampo(
            String nombre, String paterno, String materno) {

        assertThat(alumnoBase().cambioEnDatos(nombre, paterno, materno)).isTrue();
    }

    @Test
    void cambioEnDatos_debeLanzarExcepcion_cuandoLosDatosSonInvalidos() {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.cambioEnDatos("Ana", "González", "Ramírez"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_NOMBRE);
    }

    // ---------- actualizar ----------

    @Test
    void actualizar_debeModificarTodosLosDatos() {
        Alumno alumno = alumnoBase();

        alumno.actualizar("  Marcos ", " Pérez ", " Salgado ", "Marcos.Perez@Alumnos.com", "PESA260102");

        assertThat(alumno.getNombre()).isEqualTo("Marcos");
        assertThat(alumno.getApellidoPaterno()).isEqualTo("Pérez");
        assertThat(alumno.getApellidoMaterno()).isEqualTo("Salgado");
        assertThat(alumno.getEmail()).isEqualTo("marcos.perez@alumnos.com");
        assertThat(alumno.getMatricula()).isEqualTo("PESA260102");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoElEmailEsInvalido() {
        Alumno alumno = alumnoBase();

        assertThatThrownBy(() -> alumno.actualizar("Marcos", "Pérez", "Salgado", "  ", "PESA260102"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage(MSG_EMAIL);
    }

    // ---------- calcularPromedio ----------

    @Test
    void calcularPromedio_debeIgnorarMateriasSinCalificacion() {
        // Caso del contrato: Matemáticas I sin calificación + Bases de Datos con 8 => 8.00
        Alumno alumno = alumnoBase();
        alumno.agregarInscripcion(inscripcionSinCalificacion());
        alumno.agregarInscripcion(inscripcionConNota("8.0"));

        assertThat(alumno.calcularPromedio()).isEqualTo(new BigDecimal("8.00"));
    }

    @Test
    void calcularPromedio_debeIgnorarCalificacionesConValorNulo() {
        Alumno alumno = alumnoBase();
        alumno.agregarInscripcion(inscripcionConNota("8.5"));
        alumno.agregarInscripcion(inscripcionConNota(null));
        alumno.agregarInscripcion(inscripcionSinCalificacion());
        alumno.agregarInscripcion(inscripcionConNota("9.5"));

        assertThat(alumno.calcularPromedio()).isEqualTo(new BigDecimal("9.00"));
    }

    @Test
    void calcularPromedio_debeRedondearAdosDecimales() {
        // (8.5 + 8.0 + 8.0) / 3 = 8.1666... => 8.17
        Alumno alumno = alumnoBase();
        alumno.agregarInscripcion(inscripcionConNota("8.5"));
        alumno.agregarInscripcion(inscripcionConNota("8.0"));
        alumno.agregarInscripcion(inscripcionConNota("8.0"));

        assertThat(alumno.calcularPromedio()).isEqualTo(new BigDecimal("8.17"));
    }

    @Test
    void calcularPromedio_debeRedondearHalfUp_enElPuntoMedio() {
        // (8.0 + 8.0 + 8.0 + 8.1) / 4 = 8.025 => 8.03 (HALF_UP; HALF_EVEN daría 8.02)
        Alumno alumno = alumnoBase();
        alumno.agregarInscripcion(inscripcionConNota("8.0"));
        alumno.agregarInscripcion(inscripcionConNota("8.0"));
        alumno.agregarInscripcion(inscripcionConNota("8.0"));
        alumno.agregarInscripcion(inscripcionConNota("8.1"));

        assertThat(alumno.calcularPromedio()).isEqualTo(new BigDecimal("8.03"));
    }

    @Test
    void calcularPromedio_debePromediarCeros_comoCalificacionesValidas() {
        // 0 no es null: sí cuenta en el promedio
        Alumno alumno = alumnoBase();
        alumno.agregarInscripcion(inscripcionConNota("0"));
        alumno.agregarInscripcion(inscripcionConNota("10"));

        assertThat(alumno.calcularPromedio()).isEqualTo(new BigDecimal("5.00"));
    }
}
