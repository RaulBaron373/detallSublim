package com.detallsublim.app.service.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementación local del almacenamiento de archivos de Historias.
 *
 * Los archivos se guardan fuera de la base de datos y del classpath.
 */
@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootDirectory;

    public LocalFileStorageService(@Value("${application.storage.historias-location:uploads/historias}") String storageLocation) {
        this.rootDirectory = Path.of(storageLocation).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.rootDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo inicializar el directorio de almacenamiento de Historias.", e);
        }
    }

    @Override
    public String store(byte[] content, String extension) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("No se puede almacenar un archivo vacío.");
        }

        String normalizedExtension = normalizeExtension(extension);
        String storageKey = UUID.randomUUID() + "." + normalizedExtension;

        Path destination = resolveStorageKey(storageKey);

        try {
            Files.write(destination, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            return storageKey;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo almacenar el archivo.", e);
        }
    }

    @Override
    public byte[] load(String storageKey) {
        Path file = resolveStorageKey(storageKey);

        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("El archivo solicitado no existe.");
        }

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo.", e);
        }
    }

    @Override
    public void delete(String storageKey) {
        Path file = resolveStorageKey(storageKey);

        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo eliminar el archivo.", e);
        }
    }

    @Override
    public boolean exists(String storageKey) {
        return Files.isRegularFile(resolveStorageKey(storageKey));
    }

    private String normalizeExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("La extensión del archivo es obligatoria.");
        }

        String normalized = extension.toLowerCase(Locale.ROOT).strip();

        if (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }

        if (!normalized.matches("[a-z0-9]{2,5}")) {
            throw new IllegalArgumentException("La extensión del archivo no es válida.");
        }

        return normalized;
    }

    private Path resolveStorageKey(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("La clave de almacenamiento es obligatoria.");
        }

        int dotIndex = storageKey.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == storageKey.length() - 1) {
            throw new IllegalArgumentException("La clave de almacenamiento no es válida.");
        }

        String uuidPart = storageKey.substring(0, dotIndex);
        String extensionPart = storageKey.substring(dotIndex + 1);

        try {
            UUID.fromString(uuidPart);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La clave de almacenamiento no es válida.", e);
        }

        normalizeExtension(extensionPart);

        Path resolved = rootDirectory.resolve(storageKey).normalize();

        if (!resolved.startsWith(rootDirectory)) {
            throw new IllegalArgumentException("La ruta de almacenamiento no es válida.");
        }

        return resolved;
    }
}
