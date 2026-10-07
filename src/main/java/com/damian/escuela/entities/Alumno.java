package com.damian.escuela.entities;


import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ALUMNOS")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALUMNO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "MATRICULA", nullable = false, length = 10, unique = true)
    private String matricula;

    @Builder.Default
    @Column(name = "FECHA_INGRESO")
    private LocalDate fechaIngreso = LocalDate.now();

    @Builder.Default
    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    private static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno){
        StringCustomUtils.validarTamanio(nombre, 5, 50,
                "El nombre es requerido y debe tener entre 5 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 5, 50,
                "El apellido paterno es requerido y debe tener entre 5 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 5, 50,
                "El apellido materno es requerido y debe tener entre 5 y 50 caracteres");
    }

    public boolean cambioEnDatos(String nombre, String apellidoPaterno, String apellidoMaterno){
        validarDatos(nombre, apellidoPaterno, apellidoMaterno);

        return !this.nombre.equals(nombre) ||
                !this.apellidoPaterno.equals(apellidoMaterno) ||
                !this.apellidoMaterno.equals(apellidoMaterno);
    }

    public void asignarDatosAcademicos(String email, String matricula){
        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe tener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(matricula, 10, 10,
                "La matricula es requerida y debe tener exactamente 10 caracteres");

        this.email = email.toLowerCase().trim();
        this.matricula = matricula.trim();
    }

    public void agregarInscripcion(Inscripcion inscripcion){
        if (inscripcion == null)
            throw new DatoInvalidoException("La inscripción es requerida");

        this.inscripciones.add(inscripcion);
    }

    public void quitarInscripcion(Inscripcion inscripcion){
        if (inscripcion == null)
            throw new DatoInvalidoException("La inscripción es requerida");
        this.inscripciones.remove(inscripcion);
    }

    public BigDecimal calcularPromedio(){
        List<BigDecimal> calificaciones = inscripciones.stream()
                .map(Inscripcion::getCalificacion).filter(Objects::nonNull)
                .map(Calificacion::getCalificacion).filter(Objects::nonNull).toList();

        if (calificaciones.isEmpty())
            return BigDecimal.ZERO;

        BigDecimal suma = calificaciones.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(BigDecimal.valueOf(calificaciones.size()), 2, RoundingMode.HALF_UP);
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String matricula){
        asignarDatosAcademicos(email, matricula);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
    }

    public static Alumno crear(String nombre, String apellidoPaterno, String apellidoMaterno){
        validarDatos(nombre, apellidoPaterno, apellidoMaterno);

        return Alumno.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .build();
    }
}
