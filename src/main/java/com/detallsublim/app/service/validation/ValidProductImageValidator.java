package com.detallsublim.app.service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Base64;

public class ValidProductImageValidator implements ConstraintValidator<ValidProductImage, String> {

    private static final int MAX_FILE_BYTES = 1024 * 1024;

    private static final int MAX_BASE64_CHARS = ((MAX_FILE_BYTES + 2) / 3) * 4;

    private static final String JPEG_PREFIX = "data:image/jpeg;base64,";

    private static final String PNG_PREFIX = "data:image/png;base64,";

    private static final String WEBP_PREFIX = "data:image/webp;base64,";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String prefix = detectPrefix(value);

        if (prefix == null) {
            return false;
        }

        String encoded = value.substring(prefix.length());

        /*
         * Rechazamos antes de decodificar
         * para evitar reservar memoria
         * innecesariamente.
         */
        if (encoded.length() > MAX_BASE64_CHARS) {
            return false;
        }

        final byte[] decoded;

        try {
            decoded = Base64.getDecoder().decode(encoded);
        } catch (IllegalArgumentException exception) {
            return false;
        }

        if (decoded.length > MAX_FILE_BYTES) {
            return false;
        }

        if (JPEG_PREFIX.equals(prefix)) {
            return isJpeg(decoded);
        }

        if (PNG_PREFIX.equals(prefix)) {
            return isPng(decoded);
        }

        return isWebp(decoded);
    }

    private static String detectPrefix(String value) {
        if (value.startsWith(JPEG_PREFIX)) {
            return JPEG_PREFIX;
        }

        if (value.startsWith(PNG_PREFIX)) {
            return PNG_PREFIX;
        }

        if (value.startsWith(WEBP_PREFIX)) {
            return WEBP_PREFIX;
        }

        return null;
    }

    private static boolean isJpeg(byte[] bytes) {
        return bytes.length >= 3 && unsigned(bytes[0]) == 0xFF && unsigned(bytes[1]) == 0xD8 && unsigned(bytes[2]) == 0xFF;
    }

    private static boolean isPng(byte[] bytes) {
        return (
            bytes.length >= 8 &&
            unsigned(bytes[0]) == 0x89 &&
            unsigned(bytes[1]) == 0x50 &&
            unsigned(bytes[2]) == 0x4E &&
            unsigned(bytes[3]) == 0x47 &&
            unsigned(bytes[4]) == 0x0D &&
            unsigned(bytes[5]) == 0x0A &&
            unsigned(bytes[6]) == 0x1A &&
            unsigned(bytes[7]) == 0x0A
        );
    }

    private static boolean isWebp(byte[] bytes) {
        return (
            bytes.length >= 12 &&
            bytes[0] == 'R' &&
            bytes[1] == 'I' &&
            bytes[2] == 'F' &&
            bytes[3] == 'F' &&
            bytes[8] == 'W' &&
            bytes[9] == 'E' &&
            bytes[10] == 'B' &&
            bytes[11] == 'P'
        );
    }

    private static int unsigned(byte value) {
        return value & 0xFF;
    }
}
