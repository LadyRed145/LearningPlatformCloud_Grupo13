package com.duoc.learningplatformcloud.controller;

import com.duoc.learningplatformcloud.dto.CursoRequest;
import com.duoc.learningplatformcloud.dto.CursoResponse;
import com.duoc.learningplatformcloud.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public List<CursoResponse> listarCursos() {
        return cursoService.listarCursos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoResponse crearCurso(@Valid @RequestBody CursoRequest request) {
        return cursoService.crearCurso(request);
    }
}