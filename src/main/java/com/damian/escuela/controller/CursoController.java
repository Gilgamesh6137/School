package com.damian.escuela.controller;

import com.damian.escuela.dto.cursos.CursoRequest;
import com.damian.escuela.dto.cursos.CursoResponse;
import com.damian.escuela.services.cursos.CursoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Métodos para gestión de cursos")
public class CursoController extends CrudController<CursoRequest, CursoResponse, CursoService> {

    public CursoController(CursoService service){
        super(service);
    }
}
