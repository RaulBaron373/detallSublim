package com.detallsublim.app.service.validation;

import org.springframework.stereotype.Service;

/**
 * Valida las imágenes que pueden asociarse a una Historia.
 *
 * No confía únicamente en el Content-Type enviado por el cliente:
 * también verifica la firma binaria real del archivo.
 */
@Service
public class HistoriaImageValidationService {

    public static final long MAX_FILE_BYTES = 5L * 1024 * 1024;

    private static final String JPEG_CONTENT_TYPE = "image/jpeg";
    private static final String PNG_CONTENT_TYPE = "image/png";
    private static final String WEBP_CONTENT_TYPE = "image/webp";

    /**
     * Valida tamaño, MIME declarado y formato binario real.
     *
     * @param content contenido del archivo.
     * @param claimedContentType Content-Type declarado por el cliente.
     * @return información normalizada del formato validado.
     */
    public ValidatedImage validate(byte[] content, String claimedContentType) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("La imagen no puede estar vacía.");
        }

        if (content.length > MAX_FILE_BYTES) {
            throw new IllegalArgumentException("La imagen no puede superar los 5 MB.");
        }

        if (claimedContentType == null || claimedContentType.isBlank()) {
            throw new IllegalArgumentException("El tipo de contenido de la imagen es obligatorio.");
        }

        ImageFormat detectedFormat = detectFormat(content);

        if (detectedFormat == null) {
            throw new IllegalArgumentException("El archivo no es una imagen JPEG, PNG o WebP válida.");
        }

        if (!detectedFormat.contentType().equalsIgnoreCase(claimedContentType.strip())) {
            throw new IllegalArgumentException("El tipo de contenido declarado no coincide con el archivo real.");
        }

        return new ValidatedImage(detectedFormat.contentType(), detectedFormat.extension());
    }

    private ImageFormat detectFormat(byte[] bytes) {
        if (isJpeg(bytes)) {
            return new ImageFormat(JPEG_CONTENT_TYPE, "jpg");
        }

        if (isPng(bytes)) {
            return new ImageFormat(PNG_CONTENT_TYPE, "png");
        }

        if (isWebp(bytes)) {
            return new ImageFormat(WEBP_CONTENT_TYPE, "webp");
        }

        return null;
    }

    private boolean isJpeg(byte[] bytes) {
        return (
            bytes.length >= 4 &&
            unsigned(bytes[0]) == 0xFF &&
            unsigned(bytes[1]) == 0xD8 &&
            unsigned(bytes[2]) == 0xFF &&
            unsigned(bytes[bytes.length - 2]) == 0xFF &&
            unsigned(bytes[bytes.length - 1]) == 0xD9
        );
    }

    private boolean isPng(byte[] bytes) {
        return (
            bytes.length >= 24 &&
            unsigned(bytes[0]) == 0x89 &&
            unsigned(bytes[1]) == 0x50 &&
            unsigned(bytes[2]) == 0x4E &&
            unsigned(bytes[3]) == 0x47 &&
            unsigned(bytes[4]) == 0x0D &&
            unsigned(bytes[5]) == 0x0A &&
            unsigned(bytes[6]) == 0x1A &&
            unsigned(bytes[7]) == 0x0A &&
            bytes[12] == 'I' &&
            bytes[13] == 'H' &&
            bytes[14] == 'D' &&
            bytes[15] == 'R'
        );
    }

    private boolean isWebp(byte[] bytes) {
        if (
            bytes.length < 16 ||
            bytes[0] != 'R' ||
            bytes[1] != 'I' ||
            bytes[2] != 'F' ||
            bytes[3] != 'F' ||
            bytes[8] != 'W' ||
            bytes[9] != 'E' ||
            bytes[10] != 'B' ||
            bytes[11] != 'P'
        ) {
            return false;
        }

        return (
            (bytes[12] == 'V' && bytes[13] == 'P' && bytes[14] == '8' && bytes[15] == ' ') ||
            (bytes[12] == 'V' && bytes[13] == 'P' && bytes[14] == '8' && bytes[15] == 'L') ||
            (bytes[12] == 'V' && bytes[13] == 'P' && bytes[14] == '8' && bytes[15] == 'X')
        );
    }

    private int unsigned(byte value) {
        return value & 0xFF;
    }

    private record ImageFormat(String contentType, String extension) {}

    public record ValidatedImage(String contentType, String extension) {}
}
