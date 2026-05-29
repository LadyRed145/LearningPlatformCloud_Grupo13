package com.duoc.learningplatformcloud.controller;

import com.duoc.learningplatformcloud.service.ResumenInscripcionService;
import com.duoc.learningplatformcloud.service.S3ResumenService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/resumenes")
@RequiredArgsConstructor
public class ResumenController {

    private final ResumenInscripcionService resumenInscripcionService;
    private final S3ResumenService s3ResumenService;

    @PostMapping("/{inscripcionId}/generar")
    public ResponseEntity<Map<String, Object>> generarResumen(@PathVariable Long inscripcionId) {
        Long id = Objects.requireNonNull(inscripcionId, "El ID de inscripción no puede ser nulo.");

        Path archivo = resumenInscripcionService.generarArchivoResumen(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Archivo físico del resumen generado correctamente.",
                "inscripcionId", id,
                "archivo", archivo.toString()
        ));
    }

    @PostMapping("/{inscripcionId}/upload")
    public ResponseEntity<Map<String, Object>> subirResumen(@PathVariable Long inscripcionId) {
        Long id = Objects.requireNonNull(inscripcionId, "El ID de inscripción no puede ser nulo.");

        Path archivo = resumenInscripcionService.generarArchivoResumen(id);
        String key = s3ResumenService.subirResumen(id, archivo);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Resumen subido correctamente a AWS S3.",
                "inscripcionId", id,
                "s3Key", key
        ));
    }

    @PutMapping("/{inscripcionId}/upload")
    public ResponseEntity<Map<String, Object>> actualizarResumen(@PathVariable Long inscripcionId) {
        Long id = Objects.requireNonNull(inscripcionId, "El ID de inscripción no puede ser nulo.");

        Path archivo = resumenInscripcionService.generarArchivoResumen(id);
        String key = s3ResumenService.actualizarResumen(id, archivo);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resumen actualizado correctamente en AWS S3.",
                "inscripcionId", id,
                "s3Key", key
        ));
    }

    @GetMapping("/{inscripcionId}/download")
    public ResponseEntity<ByteArrayResource> descargarResumen(@PathVariable Long inscripcionId) {
        Long id = Objects.requireNonNull(inscripcionId, "El ID de inscripción no puede ser nulo.");

        ByteArrayResource recurso = s3ResumenService.descargarResumen(id);
        String nombreArchivo = s3ResumenService.construirNombreArchivo(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(nombreArchivo)
                .build());

        return new ResponseEntity<>(recurso, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{inscripcionId}")
    public ResponseEntity<Map<String, Object>> eliminarResumen(@PathVariable Long inscripcionId) {
        Long id = Objects.requireNonNull(inscripcionId, "El ID de inscripción no puede ser nulo.");

        s3ResumenService.eliminarResumen(id);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Resumen eliminado correctamente desde AWS S3.",
                "inscripcionId", id
        ));
    }
}