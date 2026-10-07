package com.damian.escuela.mapper;

import com.damian.escuela.dto.aulas.AulaRequest;
import com.damian.escuela.dto.aulas.AulaResponse;
import com.damian.escuela.dto.datos.DatosAula;
import com.damian.escuela.entities.Aula;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AulaMapper implements CommonMapper<AulaRequest, AulaResponse, Aula>{

    @Override
    public Aula requestAEntidad(AulaRequest request) {
        return request == null ? null : Aula.crear(request.nombre(), request.capacidad());
    }

    @Override
    public AulaResponse entidadAResponse(Aula entidad) {
        return entidad == null ? null : new AulaResponse(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getCapacidad()
        );
    }

    public DatosAula entidadADatosAula(Aula aula){
        return aula == null ? null : new DatosAula(aula.getNombre(), aula.getCapacidad());
    }
}
