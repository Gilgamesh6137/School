package com.damian.escuela.controller;

import com.damian.escuela.dto.grupos.GrupoRequest;
import com.damian.escuela.dto.grupos.GrupoResponse;
import com.damian.escuela.services.grupos.GrupoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grupos")
@Tag(name = "Grupos", description = "Métodos para gestión de grupos")
public class GrupoController extends CrudController<GrupoRequest, GrupoResponse, GrupoService> {

    public GrupoController(GrupoService service) {
        super(service);
    }
}
