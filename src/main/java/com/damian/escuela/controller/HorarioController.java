package com.damian.escuela.controller;

import com.damian.escuela.dto.horarios.HorarioRequest;
import com.damian.escuela.dto.horarios.HorarioResponse;
import com.damian.escuela.services.horarios.HorarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "Horarios", description = "Métodos para gestión de horarios")
public class HorarioController extends CrudController<HorarioRequest, HorarioResponse, HorarioService> {

    public HorarioController(HorarioService service) {
        super(service);
    }
}
