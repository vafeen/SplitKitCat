package org.example.project.domain.services

import org.example.project.domain.models.Config
import java.io.File

/**
 * Интерфейс для работы с файлами конфигурации.
 */
internal interface ConfigHandler {
    /**
     * Асинхронно считывает конфигурацию из файла.
     *
     * @param file Файл конфигурации.
     * @return Объект [Config] или null, если чтение не удалось.
     */
    suspend fun readConfig(file: File): Config?

    /**
     * Асинхронно записывает конфигурацию в файл.
     *
     * @param file Файл для записи конфигурации.
     * @param mainFile Основной файл.
     * @param fileParts Список частей файла.
     * @return `true`, если запись прошла успешно, иначе `false`.
     */
    suspend fun writeConfig(
        file: File,
        mainFile: File,
        fileParts: List<File>,
    ): Boolean
}