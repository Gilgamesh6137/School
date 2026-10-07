package com.damian.escuela.entities;

import com.damian.escuela.enums.DiaSemama;
import com.damian.escuela.exceptions.DatoInvalidoException;
import com.damian.escuela.utils.DateCustomUtils;
import com.damian.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "HORARIOS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HORARIO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Column(name = "DIA", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaSemama diaSemama;

    @Column(name = "HORA_INICIO", nullable = false)
    private String horaInicio;

    @Column(name = "HORA_FIN", nullable = false)
    private String horaFin;

    private static void validarDatos(String diaSemana, String horaInicio, String horaFin){
        StringCustomUtils.validarNoVacioNoNull(diaSemana, "El día de la semana es requerido");
        StringCustomUtils.validarTamanio(diaSemana, 1, 15, "El día de la semana debe tener entre 1 y 15 caracteres");

        StringCustomUtils.validarNoVacioNoNull(horaInicio, "La hora de inicio es requerida");
        DateCustomUtils.validarHora(horaInicio, "El formato de hora inicio debe ser HH:mm");

        StringCustomUtils.validarNoVacioNoNull(horaFin, "La hora de fin es requerida");
        DateCustomUtils.validarHora(horaFin, "El formato de la hora fin debe ser HH:mm");
    }

    public boolean cambioEnDatos(String diaSemana, String horaInicio, String horaFin, Grupo grupo){
        if (grupo == null)
            throw new DatoInvalidoException("El grupo es requerido");

        validarDatos(diaSemana, horaInicio, horaFin);
        return !this.diaSemama.equals(DiaSemama.obtenerDiaSemanaPorDescripcion(diaSemana)) ||
                !this.horaInicio.equals(horaInicio) ||
                !this.horaFin.equals(horaFin) ||
                !this.grupo.equals(grupo);
    }

    public String obtenerHorarioCompleto() {
        return String.format("%s %s - %s", diaSemama.getDescripcion(), horaInicio, horaFin);
    }

    public void asignarGrupo(Grupo grupo) {
        if (grupo == null) {
            throw new DatoInvalidoException("El grupo no puede ser nulo");
        }

        this.grupo = grupo;
    }

    public void actualizar(String diaSemana, String horaInicio, String horaFin, Grupo grupo){
        validarDatos(diaSemana, horaInicio, horaFin);
        this.diaSemama = DiaSemama.obtenerDiaSemanaPorDescripcion(diaSemana);
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.grupo = grupo;
    }

    public static Horario crear(String diaSemana, String horaInicio, String horaFin){
        validarDatos(diaSemana, horaInicio, horaFin);

        return Horario.builder()
                .diaSemama(DiaSemama.obtenerDiaSemanaPorDescripcion(diaSemana))
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .build();
    }
}
