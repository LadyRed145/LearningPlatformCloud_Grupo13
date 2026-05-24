package com.duoc.learningplatformcloud.repository;

import com.duoc.learningplatformcloud.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}