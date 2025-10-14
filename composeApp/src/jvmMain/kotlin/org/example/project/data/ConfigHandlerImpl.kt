package org.example.project.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.domain.models.Config
import org.example.project.domain.services.ConfigHandler
import org.example.project.domain.services.FileSplitter
import java.io.File

/**
 * Реализация интерфейса [ConfigHandler] для работы с файлами конфигурации.
 * @property fileSplitter Сервис для работы с файлами, используется для вычисления хеша.
 * @see ConfigHandler
 */
internal class ConfigHandlerImpl(
    private val fileSplitter: FileSplitter,
) : ConfigHandler {
    /**
     * Асинхронно считывает конфигурацию из файла, выбранного пользователем.
     *
     * @return Объект [Config] или null, если чтение не удалось или пользователь отменил выбор.
     */
    override suspend fun readConfig(file: File): Config? {
        val content = file.readText()
        return runCatching {
            Json.decodeFromString<Config>(content)
        }.getOrNull()
    }

    /**
     * Асинхронно записывает конфигурацию в файл, выбранный пользователем.
     *
     * @param mainFileName Имя основного файла.
     * @param fileParts Список имен частей файла.
     * @return `true`, если запись прошла успешно, иначе `false`.
     */
    override suspend fun writeConfig(file: File, mainFile: File, fileParts: List<File>): Boolean {
        println(
            "\n\n" +
                    "$file\n" +
                    "$mainFile\n" +
                    "$fileParts\n" +
                    "\n\n"
        )
//
//        val fileToSaveConfig = FileKit.openFileSaver(
//            suggestedName = "${File(mainFileName).nameWithoutExtension}.${Config.Extension}",
//            extension = Config.Extension
//        )?.file ?: return false
        val mainFile = readFileAndCreateConfigFile(mainFile)
        println()
        val parts = fileParts.map { readFileAndCreateConfigFile(it) }
        val config = Config(mainFile = mainFile, parts = parts)
        val jsonString = Json.encodeToString(config)
        file.createNewFile()
        file.writeText(jsonString)
        return true
    }

    /**
     * Считывает файл и создает для него объект [Config.File] с хешем.
     *
     * @param fileName Имя файла для обработки.
     * @return Объект [Config.File].
     */
    private fun readFileAndCreateConfigFile(file: File): Config.File {
        val hash = fileSplitter.sha256sum(file)
        return Config.File(name = file.name, hash = hash)
    }
}