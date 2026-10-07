package com.damian.escuela.controller;

import com.damian.escuela.dto.calificacion.CalificacionRequest;
import com.damian.escuela.dto.calificacion.CalificacionResponse;
import com.damian.escuela.services.calificacion.CalificacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calificacion")
@Tag(name = "Calificación", description = "Métodos para gestión de calificaciones")
public class CalificacionController extends CrudController<CalificacionRequest, CalificacionResponse, CalificacionService> {

    public CalificacionController(CalificacionService service) {
        super(service);
    }
}
