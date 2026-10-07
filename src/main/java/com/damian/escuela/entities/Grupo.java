package com.damian.escuela.entities;

import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.utils.DateCustomUtils;
import com.damian.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GRUPOS", uniqueConstraints = {
        @UniqueConstraint(
                name = "GRUPO_CU_MA_AU_PE_UK",
                columnNames = {"ID_CURSO", "ID_MAESTRO", "ID_AULA", "PERIODO"}
        )
})
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPO")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CURSO", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAESTRO", nullable = false)
    private Maestro maestro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AULA", nullable = false)
    private Aula aula;

    @Column(name = "PERIODO", length = 20, nullable = false)
    private String periodo;

    @Builder.Default
    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    private List<Horario> horarios = new ArrayList<>();

    private static void validarDatos(String periodo){
        StringCustomUtils.validarTamanio(periodo, 1, 20,
                "El periodo es requerido y debe tener entre 1 y 20 caracteres");

        DateCustomUtils.validarFormatoPeriodo(periodo, "El periodo debe tener el formato YYYY-MM");
    }

    public boolean cambioEnDatos(Curso curso, Maestro maestro, Aula aula, String periodo){
        if (curso == null || maestro == null || aula == null)
            throw new DatoInvalidoException("El curso, el maestro y el aula son requeridos");

        validarDatos(periodo);
        return !this.periodo.equals(periodo) || !this.curso.equals(curso) || !this.maestro.equals(maestro) || !this.aula.equals(aula);
    }

    public void asignarCursoMaestroAula(Curso curso, Maestro maestro, Aula aula){
        if (curso == null)
            throw new DatoInvalidoException("El curso es requerido");

        if (maestro == null)
            throw new DatoInvalidoException("El maestro es requerido");

        if (aula == null)
            throw new DatoInvalidoException("El aula es requerida");

        this.curso = curso;
        this.maestro = maestro;
        this.aula = aula;
    }

    public void agregarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null)
            throw new DatoInvalidoException("La inscripción es requerida");

        this.inscripciones.add(inscripcion);
    }

    public void quitarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null)
            throw new DatoInvalidoException("La inscripción es requerida");

        this.inscripciones.remove(inscripcion);
    }

    public void actualizarDatos(Curso curso, Maestro maestro, Aula aula, String periodo){
        validarDatos(periodo);
        this.periodo = periodo;
        asignarCursoMaestroAula(curso, maestro, aula);
    }

    public static Grupo crear(String periodo){
        validarDatos(periodo);
        return Grupo.builder()
                .periodo(periodo)
                .build();
    }
}
