package com.duoc.learningplatformcloud.dto;

import jakarta.validation.constraints.*;

public record CursoRequest(
        @NotBlank String nombre,
        @NotBlank String instructor,
        @NotNull @Min(1) Integer duracionHoras,
        @NotNull @PositiveOrZero Double costo
) {
}