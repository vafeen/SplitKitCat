package org.example.project.domain.services

import org.example.project.domain.models.FileInfo

/**
 * Интерфейс для предварительного просмотра файлов в файловой системе.
 */
internal interface FilePeeker {
    /**
     * Асинхронно получает список файлов для предварительного просмотра.
     *
     * @return Список объектов [FileInfo], представляющих доступные файлы, или null, если произошла ошибка.
     */
    suspend fun peekFile(): FileInfo?
    suspend fun peekConfig(): FileInfo?
    suspend fun peekFileForSaving(
        suggestedName: String,
        extension: String?,
    ): FileInfo?
}