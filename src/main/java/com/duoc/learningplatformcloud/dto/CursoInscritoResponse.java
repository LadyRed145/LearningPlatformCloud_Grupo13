package com.duoc.learningplatformcloud.dto;

public record CursoInscritoResponse(
        Long id,
        String nombre,
        String instructor,
        Integer duracionHoras,
        Double costo
) {
}