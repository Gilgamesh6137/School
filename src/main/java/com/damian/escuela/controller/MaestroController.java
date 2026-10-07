package com.damian.escuela.controller;

import com.damian.escuela.dto.maestros.MaestroRequest;
import com.damian.escuela.dto.maestros.MaestroResponse;
import com.damian.escuela.services.maestros.MaestroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maestros")
@Tag(name = "Maestros", description = "Métodos para gestión de maestros")
public class MaestroController extends CrudController<MaestroRequest, MaestroResponse, MaestroService>{

    public MaestroController(MaestroService service){
        super(service);
    }
}
