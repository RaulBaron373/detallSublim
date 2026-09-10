package com.detallsublim.app.service.storage;

/**
 * Abstracción para almacenar archivos fuera de la base de datos.
 *
 * Las implementaciones pueden utilizar almacenamiento local,
 * S3, Cloudflare R2 u otro proveedor compatible.
 */
public interface FileStorageService {
    /**
     * Almacena un archivo y devuelve una clave interna única.
     *
     * @param content contenido ya validado del archivo.
     * @param extension extensión normalizada, sin punto.
     * @return clave interna del archivo almacenado.
     */
    String store(byte[] content, String extension);

    /**
     * Recupera el contenido de un archivo por su clave interna.
     *
     * @param storageKey clave interna.
     * @return contenido del archivo.
     */
    byte[] load(String storageKey);

    /**
     * Elimina físicamente un archivo.
     *
     * @param storageKey clave interna.
     */
    void delete(String storageKey);

    /**
     * Comprueba si el archivo existe físicamente.
     *
     * @param storageKey clave interna.
     * @return true si existe.
     */
    boolean exists(String storageKey);
}
