package org.example.project.domain.services

import org.example.project.domain.models.Config

/**
 * Интерфейс для работы с файлами конфигурации.
 */
internal interface ConfigHandler {
    /**
     * Асинхронно считывает конфигурацию из файла.
     *
     * @return Объект [Config] или null, если чтение не удалось.
     */
    suspend fun readConfig(): Config?

    /**
     * Асинхронно записывает конфигурацию в файл.
     *
     * @param mainFileName Имя основного файла.
     * @param fileParts Список имен частей файла.
     * @return `true`, если запись прошла успешно, иначе `false`.
     */
    suspend fun writeConfig(
        mainFileName: String,
        fileParts: List<String>,
    ): Boolean
}