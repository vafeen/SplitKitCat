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
internal class FileSplitterImpl() : FileSplitter {
    /**
     * Вычисляет контрольную сумму SHA-256 для указанного файла.
     *
     * @param file Файл, для которого необходимо вычислить контрольную сумму.
     * @return Строковое представление контрольной суммы SHA-256 в шестнадцатеричном формате.
     * @throws Exception если файл не существует.
     */
    override fun sha256sum(file: File): String {
        if (!file.exists()) throw Exception("File ${file.name} is not exists in FileSplitterImpl.sha256sum")
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
     * @param inputFile Исходный файл, который нужно разбить.
     * @param outputDirPath Путь к директории, куда будут сохранены части файла.
     * @param chunkSize Размер каждой части в байтах.
     * @return Список созданных файлов-частей.
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
     *
     * @param partsDir Директория, содержащая части файла.
     * @param outputFile Файл, в который будут объединены части.
     * @param baseFileName Исходное имя файла, которое использовалось для создания частей.
     */
    override suspend fun catFiles(
        partsDir: File,
        outputFile: File,
        baseFileName: String
    ) {
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

    /**
     * Компаньон-объект для [FileSplitterImpl].
     */
    companion object {
        /**
         * Постфикс для имен файлов-частей.
         */
        private const val APP_PART_POSTFIX = "_kit-cat-part"
    }

}
