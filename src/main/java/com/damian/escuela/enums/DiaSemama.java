package com.damian.escuela.enums;

import com.damian.escuela.exceptions.RecursoNoEncontradoException;
import com.damian.escuela.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaSemama {

    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sábado");

    private final String descripcion;

    public static DiaSemama obtenerDiaSemanaPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");
        String descripcionNormalizada = StringCustomUtils.quitarAcentos(descripcion);

        for (DiaSemama diaSemama : values()){
            if (StringCustomUtils.quitarAcentos(diaSemama.descripcion).equalsIgnoreCase(descripcionNormalizada))
                return diaSemama;
        }

        throw new RecursoNoEncontradoException("No exixte un dia de la semana con la descripción: " + descripcion);
    }
}
