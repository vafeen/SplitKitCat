package org.example.project.domain.services

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
    fun sha256sum(filepath: String): String

    /**
     * Разбивает файл на несколько частей заданного размера.
     *
     * @param inputFilePath Путь к исходному файлу, который нужно разбить.
     * @param outputDirPath Путь к директории, куда будут сохранены части файла.
     * @param chunkSize Размер каждой части в байтах.
     * // возвращает список имен частей файлов
     */
    suspend fun splitFile(
        inputFilePath: String,
        outputDirPath: String,
        chunkSize: Int
    ): List<String>

    /**
     * Объединяет части файла обратно в один целый файл.
     *
     * @param outputFilePath Путь к директории для сохранения итогового файла.
     * @param partsDirPath Путь к директории, содержащей части файла.
     * @param baseFileName Исходное имя файла, которое использовалось для создания частей.
     */
    fun catFiles(outputFilePath: String, partsDirPath: String, baseFileName: String)
}