package com.duoc.learningplatformcloud.service;

import com.duoc.learningplatformcloud.exception.RecursoNoEncontradoException;
import com.duoc.learningplatformcloud.model.DetalleInscripcion;
import com.duoc.learningplatformcloud.model.Inscripcion;
import com.duoc.learningplatformcloud.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ResumenInscripcionService {

    private static final String CARPETA_RESUMENES = "resumenes";

    private final InscripcionRepository inscripcionRepository;

    @Transactional(readOnly = true)
    public Path generarArchivoResumen(Long inscripcionId) {
        if (inscripcionId == null) {
            throw new IllegalArgumentException("El ID de inscripción no puede ser nulo.");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una inscripción con ID: " + inscripcionId
                ));

        String contenido = construirContenidoResumen(inscripcion);

        try {
            Path carpetaResumenes = Path.of(CARPETA_RESUMENES);
            Files.createDirectories(carpetaResumenes);

            Path archivo = carpetaResumenes.resolve("Resumen_" + inscripcion.getId() + ".txt");

            Files.writeString(archivo, contenido, StandardCharsets.UTF_8);

            return archivo;

        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible generar el archivo físico del resumen.", ex);
        }
    }

    private String construirContenidoResumen(Inscripcion inscripcion) {
        StringBuilder resumen = new StringBuilder();

        resumen.append("RESUMEN DE INSCRIPCIÓN").append(System.lineSeparator());
        resumen.append("=======================").append(System.lineSeparator());
        resumen.append("Número de resumen: ").append(inscripcion.getId()).append(System.lineSeparator());
        resumen.append("Estudiante: ").append(inscripcion.getEstudiante()).append(System.lineSeparator());
        resumen.append("Fecha de inscripción: ").append(inscripcion.getFechaInscripcion()).append(System.lineSeparator());
        resumen.append(System.lineSeparator());

        resumen.append("Cursos inscritos:").append(System.lineSeparator());

        for (DetalleInscripcion detalle : inscripcion.getDetalles()) {
            resumen.append("- ")
                    .append(detalle.getCurso().getNombre())
                    .append(" | Instructor: ")
                    .append(detalle.getCurso().getInstructor())
                    .append(" | Duración: ")
                    .append(detalle.getCurso().getDuracionHoras())
                    .append(" horas")
                    .append(" | Costo: $")
                    .append(detalle.getCostoCurso())
                    .append(System.lineSeparator());
        }

        resumen.append(System.lineSeparator());
        resumen.append("Total inscripción: $").append(inscripcion.getTotal()).append(System.lineSeparator());

        return resumen.toString();
    }
}