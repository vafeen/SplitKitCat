package org.example.project.domain.models

import java.io.File

/**
 * Представляет информацию о файле.
 *
 * @property name Имя файла.

 * @property size Размер файла в байтах.
 */
internal data class FileInfo(
    val name: String,
    val file: File,
    val size: Long,
)