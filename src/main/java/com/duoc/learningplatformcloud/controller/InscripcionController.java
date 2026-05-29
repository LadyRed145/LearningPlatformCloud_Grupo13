package com.duoc.learningplatformcloud.controller;

import com.duoc.learningplatformcloud.dto.InscripcionRequest;
import com.duoc.learningplatformcloud.dto.InscripcionResponse;
import com.duoc.learningplatformcloud.service.InscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @GetMapping
    public List<InscripcionResponse> listarInscripciones() {
        return inscripcionService.listarInscripciones();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse inscribirEstudiante(@Valid @RequestBody InscripcionRequest request) {
        return inscripcionService.inscribirEstudiante(request);
    }
}