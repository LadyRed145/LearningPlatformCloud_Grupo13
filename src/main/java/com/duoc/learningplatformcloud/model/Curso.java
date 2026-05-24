package com.duoc.learningplatformcloud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String instructor;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer duracionHoras;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double costo;
}