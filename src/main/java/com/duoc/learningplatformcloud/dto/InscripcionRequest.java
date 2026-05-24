package com.duoc.learningplatformcloud.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record InscripcionRequest(
        @NotBlank String estudiante,
        @NotEmpty List<Long> cursosIds
) {
}