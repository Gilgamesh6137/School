package com.damian.escuela.controller;

import com.damian.escuela.dto.inscripciones.InscripcionRequest;
import com.damian.escuela.dto.inscripciones.InscripcionResponse;
import com.damian.escuela.services.inscripciones.InscripcionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones", description = "Métodos para gestión de inscripciones")
public class InscripcionController extends CrudController<InscripcionRequest, InscripcionResponse, InscripcionService> {

    public InscripcionController(InscripcionService service) {
        super(service);
    }
}
