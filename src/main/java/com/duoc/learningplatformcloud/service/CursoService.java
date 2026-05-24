package com.duoc.learningplatformcloud.service;

import com.duoc.learningplatformcloud.dto.CursoRequest;
import com.duoc.learningplatformcloud.dto.CursoResponse;
import com.duoc.learningplatformcloud.model.Curso;
import com.duoc.learningplatformcloud.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    public List<CursoResponse> listarCursos() {
        return cursoRepository.findAll()
                .stream()
                .map(this::mapearCursoResponse)
                .toList();
    }

    public CursoResponse crearCurso(CursoRequest request) {
        Curso curso = Curso.builder()
                .nombre(request.nombre())
                .instructor(request.instructor())
                .duracionHoras(request.duracionHoras())
                .costo(request.costo())
                .build();

        Curso cursoGuardado = guardarCurso(curso);

        return mapearCursoResponse(cursoGuardado);
    }

    @SuppressWarnings("null")
    private Curso guardarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    private CursoResponse mapearCursoResponse(Curso curso) {
        return new CursoResponse(
                curso.getId(),
                curso.getNombre(),
                curso.getInstructor(),
                curso.getDuracionHoras(),
                curso.getCosto()
        );
    }
}