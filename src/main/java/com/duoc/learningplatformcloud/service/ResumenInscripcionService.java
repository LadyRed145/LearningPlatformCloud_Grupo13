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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ResumenInscripcionService {

    private static final String CARPETA_RESUMENES = "resumenes";
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InscripcionRepository inscripcionRepository;

    @Transactional(readOnly = true)
    public Path generarArchivoResumen(Long inscripcionId) {
        Inscripcion inscripcion = buscarInscripcion(inscripcionId);
        String contenido = construirContenidoResumen(inscripcion);

        return escribirArchivo(inscripcion.getId(), contenido);
    }

    @Transactional(readOnly = true)
    public Path generarArchivoResumenActualizado(
            Long inscripcionId,
            String observacion,
            String contenidoExtra
    ) {
        Inscripcion inscripcion = buscarInscripcion(inscripcionId);

        String contenido = construirContenidoResumen(inscripcion)
                + System.lineSeparator()
                + "ACTUALIZACIÓN DEL RESUMEN"
                + System.lineSeparator()
                + "========================"
                + System.lineSeparator()
                + "Fecha de actualización: "
                + LocalDateTime.now().format(FORMATO_FECHA)
                + System.lineSeparator()
                + "Origen del cambio: Endpoint PUT /api/resumenes/"
                + inscripcion.getId()
                + "/upload"
                + System.lineSeparator()
                + "Observación: "
                + observacion
                + System.lineSeparator()
                + "Contenido extra: "
                + contenidoExtra
                + System.lineSeparator()
                + "Estado: Archivo modificado y sobrescrito realmente en AWS S3."
                + System.lineSeparator();

        return escribirArchivo(inscripcion.getId(), contenido);
    }

    private Inscripcion buscarInscripcion(Long inscripcionId) {
        if (inscripcionId == null) {
            throw new IllegalArgumentException("El ID de inscripción no puede ser nulo.");
        }

        return inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una inscripción con ID: " + inscripcionId
                ));
    }

    private Path escribirArchivo(Long inscripcionId, String contenido) {
        try {
            Path carpetaResumenes = Path.of(CARPETA_RESUMENES);
            Files.createDirectories(carpetaResumenes);

            Path archivo = carpetaResumenes.resolve("Resumen_" + inscripcionId + ".txt");

            Files.writeString(
                    archivo,
                    contenido,
                    StandardCharsets.UTF_8
            );

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