package com.duoc.learningplatformcloud.service;

import com.duoc.learningplatformcloud.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Path;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3ResumenService {

    private static final String CONTENT_TYPE_TEXT = "text/plain";

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String subirResumen(Long resumenId, Path archivoResumen) {
        validarParametros(resumenId, archivoResumen);

        String key = construirKey(resumenId);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(CONTENT_TYPE_TEXT)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(archivoResumen));

        return key;
    }

    public String actualizarResumen(Long resumenId, Path archivoResumen) {
        validarParametros(resumenId, archivoResumen);

        String key = construirKey(resumenId);

        validarExistencia(key);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(CONTENT_TYPE_TEXT)
                .build();

        s3Client.putObject(request, RequestBody.fromFile(archivoResumen));

        return key;
    }

    public ByteArrayResource descargarResumen(Long resumenId) {
        if (resumenId == null) {
            throw new IllegalArgumentException("El ID del resumen no puede ser nulo.");
        }

        String key = construirKey(resumenId);

        validarExistencia(key);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> archivo = s3Client.getObjectAsBytes(request);
        byte[] contenido = Objects.requireNonNull(
                archivo.asByteArray(),
                "El archivo descargado desde S3 no puede ser nulo."
        );

        return new ByteArrayResource(contenido);
    }

    public void eliminarResumen(Long resumenId) {
        if (resumenId == null) {
            throw new IllegalArgumentException("El ID del resumen no puede ser nulo.");
        }

        String key = construirKey(resumenId);

        validarExistencia(key);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    public String construirNombreArchivo(Long resumenId) {
        if (resumenId == null) {
            throw new IllegalArgumentException("El ID del resumen no puede ser nulo.");
        }

        return "Resumen_" + resumenId + ".txt";
    }

    private String construirKey(Long resumenId) {
        return resumenId + "/" + construirNombreArchivo(resumenId);
    }

    private void validarParametros(Long resumenId, Path archivoResumen) {
        if (resumenId == null) {
            throw new IllegalArgumentException("El ID del resumen no puede ser nulo.");
        }

        if (archivoResumen == null) {
            throw new IllegalArgumentException("El archivo del resumen no puede ser nulo.");
        }
    }

    private void validarExistencia(String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(request);

        } catch (NoSuchKeyException ex) {
            throw new RecursoNoEncontradoException("El archivo no existe en S3: " + key);

        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                throw new RecursoNoEncontradoException("El archivo no existe en S3: " + key);
            }

            throw ex;
        }
    }
}