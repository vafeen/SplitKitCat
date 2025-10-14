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
     * @param filepath Путь к файлу, для которого необходимо вычислить контрольную сумму.
     * @return Строковое представление контрольной суммы SHA-256 в шестнадцатеричном формате.
     */
    fun sha256sum(file: File): String

    /**
     * Разбивает файл на несколько частей заданного размера.
     *
     * @param inputFilePath Путь к исходному файлу, который нужно разбить.
     * @param outputDirPath Путь к директории, куда будут сохранены части файла.
     * @param chunkSize Размер каждой части в байтах.
     * // возвращает список имен частей файлов
     */
    suspend fun splitFile(
        inputFile: File,
        outputDirPath: String,
        chunkSize: Int
    ): List<File>

    /**
     * Объединяет части файла обратно в один целый файл.
     *
     * @param baseFileName Исходное имя файла, которое использовалось для создания частей.
     */
    suspend fun catFiles(
        partsDir: File,
        outputFile: File,
        baseFileName: String
    )
}