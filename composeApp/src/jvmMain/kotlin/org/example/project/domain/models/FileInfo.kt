package org.example.project.domain.models

/**
 * Представляет информацию о файле.
 *
 * @property name Имя файла.
 * @property pathToFile Путь к директории, в которой находится файл.
 * @property size Размер файла в байтах.
 */
internal data class FileInfo(
    val name: String,
    val pathToFile: String,
    val size: Long,
) {
    /**
     * Полный путь к файлу, включая имя файла.
     */
    val fileWithPath: String
        get() = "$pathToFile\\$name"
}