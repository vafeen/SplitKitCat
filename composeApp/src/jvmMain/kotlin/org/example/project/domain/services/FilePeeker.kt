package org.example.project.domain.services

import org.example.project.domain.models.FileInfo

/**
 * Интерфейс для выбора файлов в файловой системе.
 */
internal interface FilePeeker {
    /**
     * Асинхронно открывает диалог для выбора одного файла.
     *
     * @return [FileInfo] выбранного файла или null, если выбор был отменен.
     */
    suspend fun peekFile(): FileInfo?

    /**
     * Асинхронно открывает диалог для выбора файла конфигурации.
     *
     * @return [FileInfo] выбранного файла конфигурации или null, если выбор был отменен.
     */
    suspend fun peekConfig(): FileInfo?

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
    ): FileInfo?
}