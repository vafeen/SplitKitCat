package org.example.project.domain.services

import java.io.File

/**
 * Интерфейс для обработки файлов, включая вычисление контрольных сумм,
 * разделение и объединение файлов.
 */
internal interface FileSplitter {
    /**
     * Вычисляет контрольную сумму SHA-256 для указанного файла.
     *
     * @param file Файл, для которого необходимо вычислить контрольную сумму.
     * @return Строковое представление контрольной суммы SHA-256 в шестнадцатеричном формате.
     */
    fun sha256sum(file: File): String

    /**
     * Разбивает файл на несколько частей заданного размера.
     *
     * @param inputFile Исходный файл, который нужно разбить.
     * @param outputDirPath Путь к директории, куда будут сохранены части файла.
     * @param chunkSize Размер каждой части в байтах.
     * @return Список созданных файлов-частей.
     */
    suspend fun splitFile(
        inputFile: File,
        outputDirPath: String,
        chunkSize: Long
    ): List<File>

    /**
     * Объединяет части файла обратно в один целый файл.
     *
     * @param partsDir Директория, содержащая части файла.
     * @param outputFile Файл, в который будут объединены части.
     * @param baseFileName Исходное имя файла, которое использовалось для создания частей.
     */
    suspend fun catFiles(
        partsDir: File,
        outputFile: File,
        baseFileName: String
    )
}