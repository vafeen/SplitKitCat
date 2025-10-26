package org.example.project.data

import org.example.project.domain.services.FileSplitter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import kotlin.math.min

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
        chunkSize: Long
    ): List<File> {
        // Создаем объект File для выходной директории.
        val outputDir = File(outputDirPath)
        // Если директория не существует, создаем ее.
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        // Если размер части некорректен или исходный файл пуст, возвращаем пустой список.
        if (chunkSize <= 0L || inputFile.length() == 0L) {
            return emptyList()
        }

        // Создаем изменяемый список для хранения созданных частей файла.
        val parts = mutableListOf<File>()
        // Создаем буфер фиксированного размера для чтения данных. Это позволяет избежать проблем с памятью.
        val buffer = ByteArray(8 * 1024) // Use a fixed-size buffer
        // Инициализируем счетчик для нумерации частей файла.
        var partNumber = 0
        // Инициализируем счетчик общего количества прочитанных байт из исходного файла.
        var totalBytesRead: Long = 0

        // Открываем FileInputStream для исходного файла. `use` гарантирует автоматическое закрытие потока.
        FileInputStream(inputFile).use { inputStream ->
            // Запускаем цикл, который будет выполняться, пока не будут прочитаны все байты исходного файла.
            while (totalBytesRead < inputFile.length()) {
                // Создаем файл для очередной части с уникальным именем.
                val partFile = File(outputDir, "${inputFile.name}${APP_PART_POSTFIX}$partNumber")
                // Добавляем созданный файл в список частей.
                parts.add(partFile)
                // Увеличиваем номер части для следующей итерации.
                partNumber++

                // Открываем FileOutputStream для записи в текущую часть файла. `use` также закроет поток автоматически.
                FileOutputStream(partFile).use { outputStream ->
                    // Определяем, сколько байт нужно записать в эту часть. Это либо полный `chunkSize`, либо остаток файла.
                    val bytesForThisPart = min(chunkSize, inputFile.length() - totalBytesRead)
                    // Инициализируем счетчик байт, записанных в текущую часть.
                    var bytesWrittenForPart: Long = 0

                    // Запускаем вложенный цикл, который выполняется, пока текущая часть не будет заполнена.
                    while (bytesWrittenForPart < bytesForThisPart) {
                        // Определяем, сколько байт нужно прочитать за одну итерацию. Это минимум из размера буфера и остатка для текущей части.
                        val toRead = min(
                            buffer.size.toLong(),
                            bytesForThisPart - bytesWrittenForPart
                        ).toInt()
                        // Читаем байты из исходного файла в буфер.
                        val bytesRead = inputStream.read(buffer, 0, toRead)
                        // Если достигнут конец файла, прерываем цикл.
                        if (bytesRead == -1) break

                        // Записываем прочитанные байты из буфера в файл текущей части.
                        outputStream.write(buffer, 0, bytesRead)
                        // Увеличиваем счетчик записанных в эту часть байт.
                        bytesWrittenForPart += bytesRead
                    }
                    // Увеличиваем общий счетчик прочитанных байт на количество байт, записанных в последнюю часть.
                    totalBytesRead += bytesWrittenForPart
                }
            }
        }
        // Возвращаем список созданных файлов-частей.
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
