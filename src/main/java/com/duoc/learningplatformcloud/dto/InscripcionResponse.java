package com.duoc.learningplatformcloud.dto;

import java.time.LocalDateTime;
import java.util.List;

public record InscripcionResponse(
        Long id,
        String estudiante,
        List<CursoInscritoResponse> cursos,
        Double total,
        LocalDateTime fechaInscripcion
) {
}