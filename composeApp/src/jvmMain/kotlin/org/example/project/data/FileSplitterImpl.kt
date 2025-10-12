package org.example.project.data

import org.example.project.domain.services.FileSplitter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Реализация интерфейса [FileSplitter] для обработки файлов.
 * @see FileSplitter
 */
internal class FileSplitterImpl : FileSplitter {
    /**
     * Вычисляет контрольную сумму SHA-256 для указанного файла.
     *
     * @param filepath Путь к файлу, для которого необходимо вычислить контрольную сумму.
     * @return Строковое представление контрольной суммы SHA-256 в шестнадцатеричном формате.
     */
    override fun sha256sum(filepath: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(File(filepath)).use { fis ->
            val buffer = ByteArray(1024 * 8)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Разбивает файл на несколько частей заданного размера.
     *
     * @param inputFilePath Путь к исходному файлу, который нужно разбить.
     * @param outputDirPath Путь к директории, куда будут сохранены части файла.
     * @param chunkSize Размер каждой части в байтах.
     */
    override fun splitFile(inputFilePath: String, outputDirPath: String, chunkSize: Int) {
        val inputFile = File(inputFilePath)
        val outputDir = File(outputDirPath)

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        FileInputStream(inputFile).use { inputStream ->
            var partNumber = 0
            val buffer = ByteArray(chunkSize)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                val partFile = File(outputDir, "${inputFile.name}_part$partNumber")
                FileOutputStream(partFile).use { outputStream ->
                    outputStream.write(buffer, 0, bytesRead)
                }
                partNumber++
            }
        }
    }

    /**
     * Объединяет части файла обратно в один целый файл.
     *
     * @param outputFilePath Путь к директории для сохранения итогового файла.
     * @param partsDirPath Путь к директории, содержащей части файла.
     * @param baseFileName Исходное имя файла, которое использовалось для создания частей.
     */
    override fun catFiles(outputFilePath: String, partsDirPath: String, baseFileName: String) {
        val outputFile = File("$outputFilePath/$baseFileName")
        val partsDir = File(partsDirPath)

        val partFiles = partsDir.listFiles { _, name ->
            name.startsWith(baseFileName) &&
                    name.contains("part")
        }?.sortedBy { it.name } ?: return

        FileOutputStream(outputFile).use { outputStream ->
            partFiles.forEach { partFile ->
                partFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

}