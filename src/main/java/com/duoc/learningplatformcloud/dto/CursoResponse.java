package com.duoc.learningplatformcloud.dto;

public record CursoResponse(
        Long id,
        String nombre,
        String instructor,
        Integer duracionHoras,
        Double costo
) {
}