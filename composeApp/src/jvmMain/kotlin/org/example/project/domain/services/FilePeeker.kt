package org.example.project.domain.services

import org.example.project.domain.models.File

/**
 * Интерфейс для предварительного просмотра файлов в файловой системе.
 */
internal interface FilePeeker {
    /**
     * Асинхронно получает список файлов для предварительного просмотра.
     *
     * @return Список объектов [File], представляющих доступные файлы, или null, если произошла ошибка.
     */
    suspend fun peekFiles(): List<File>?
}