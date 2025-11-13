package org.example.project.domain.services

import java.io.File

/**
 * Интерфейс для выбора файлов в файловой системе.
 */
internal interface FilePeeker {
    /**
     * Асинхронно открывает диалог для выбора одного файла.
     *
     * @return [FileInfo] выбранного файла или null, если выбор был отменен.
     */
    suspend fun peekFile(): File?

    /**
     * Асинхронно открывает диалог для выбора файла конфигурации.
     *
     * @return [FileInfo] выбранного файла конфигурации или null, если выбор был отменен.
     */
    suspend fun peekConfig(): File?

    /**
     * Асинхронно открывает диалог для сохранения файла.
     *
     * @param suggestedName Предлагаемое имя файла.
     * @param extension Предлагаемое расширение файла.
     * @return [FileInfo] для сохранения файла или null, если выбор был отменен.
     */
    suspend fun peekFileForSaving(
        suggestedName: String,
        extension: String?,
    ): File?
}