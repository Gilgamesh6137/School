package com.damian.escuela.entities;

import com.damian.escuela.utils.StringCustomUtils;
import com.damian.escuela.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CURSOS")
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CURSO")
    private Long id;

    @Column(name = "NOMBRE", length = 100, nullable = false, unique = true)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 200, nullable = false)
    private String descripcion;

    @Column(name = "CREDITOS", nullable = false)
    private Integer creditos;

    private static void validarDatos(String nombre, String descripcion, Integer creditos){
        StringCustomUtils.validarTamanio(nombre, 1, 100,
                "El nombre es requerido y debe tener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(descripcion, 1, 200,
                "La descripción es requerida y debe tener entre 1 y 200 caracteres");

        ValoresNumericosUtils.validarEnteroPositivo(creditos,
                "Los creditos son requeridos y deden ser positivos");
    }

    public void actualizar(String nombre, String descripcion, Integer creditos){
        validarDatos(nombre, descripcion, creditos);

        this.nombre = nombre.trim();
        this.descripcion = descripcion.trim();
        this.creditos = creditos;
    }

    public static Curso crear(String nombre, String descripcion, Integer creditos){
        validarDatos(nombre, descripcion, creditos);

        return Curso.builder()
                .nombre(nombre.trim())
                .descripcion(descripcion.trim())
                .creditos(creditos)
                .build();
    }
}
