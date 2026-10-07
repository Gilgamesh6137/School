package com.damian.escuela.controller;

import com.damian.escuela.dto.aulas.AulaRequest;
import com.damian.escuela.dto.aulas.AulaResponse;
import com.damian.escuela.services.aulas.AulaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aulas")
@Tag(name = "Aulas", description = "Métodos para gestión de aulas")
public class AulaController extends CrudController<AulaRequest, AulaResponse, AulaService> {

    public AulaController(AulaService service){
        super(service);
    }
}
