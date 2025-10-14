package org.example.project.data

import org.example.project.domain.services.FilePeeker
import org.example.project.domain.services.FileSplitter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Реализация интерфейса [FileSplitter] для обработки файлов.
 * @see FileSplitter
 */
internal class FileSplitterImpl(
    private val filePeeker: FilePeeker,
) : FileSplitter {
    /**
     * Вычисляет контрольную сумму SHA-256 для указанного файла.
     *
     * @param filepath Путь к файлу, для которого необходимо вычислить контрольную сумму.
     * @return Строковое представление контрольной суммы SHA-256 в шестнадцатеричном формате.
     */
    override fun sha256sum(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
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
    override suspend fun splitFile(
        inputFile: File,
        outputDirPath: String,
        chunkSize: Int
    ): List<File> {
        val outputDir = File(outputDirPath)
        val parts = mutableListOf<File>()

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        FileInputStream(inputFile).use { inputStream ->
            var partNumber = 0
            val buffer = ByteArray(chunkSize)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                val partFile = File(
                    outputDir,
                    "${inputFile.name}${APP_PART_POSTFIX}$partNumber"
                ).also { parts.add(it) }
                FileOutputStream(partFile).use { outputStream ->
                    outputStream.write(buffer, 0, bytesRead)
                }
                partNumber++
            }
        }
        return parts
    }

    /**
     * Объединяет части файла обратно в один целый файл.

     * @param baseFileName Исходное имя файла, которое использовалось для создания частей.
     */
    override suspend fun catFiles(baseFileName: String) {
        val outputFile = filePeeker.peekFileForSaving(
            suggestedName = baseFileName,
            extension = File(baseFileName).extension
        )?.file ?: return


        val partsDir = File(outputFile.parent)

        val partFiles = partsDir.listFiles { _, name ->
            name.startsWith(baseFileName) &&
                    name.contains(APP_PART_POSTFIX)
        }?.sortedBy { it.name } ?: return

        FileOutputStream(outputFile).use { outputStream ->
            partFiles.forEach { partFile ->
                partFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    companion object {
        private const val APP_PART_POSTFIX = "_kit-cat-part"
    }

}