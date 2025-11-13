package org.example.project.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.domain.models.Config
import org.example.project.domain.services.ConfigHandler
import org.example.project.domain.services.FileHasher
import java.io.File

/**
 * Реализация интерфейса [ConfigHandler] для работы с файлами конфигурации.
 * @property fileSplitter Сервис для работы с файлами, используется для вычисления хеша.
 * @see ConfigHandler
 */
internal class ConfigHandlerImpl(
    private val fileHasher: FileHasher
) : ConfigHandler {
    /**
     * Асинхронно считывает конфигурацию из указанного файла.
     *
     * @param file Файл конфигурации для чтения.
     * @return Объект [Config] или null, если чтение не удалось.
     */
    override suspend fun readConfig(file: File): Config? = runCatching {
        Json.decodeFromString<Config>(file.readText())
        }.getOrNull()

    /**
     * Асинхронно записывает конфигурацию в указанный файл.
     *
     * @param file Файл для записи конфигурации.
     * @param mainFile Основной файл, который был разделен.
     * @param fileParts Список частей файла.
     * @return `true`, если запись прошла успешно, иначе `false`.
     */
    override suspend fun writeConfig(file: File, mainFile: File, fileParts: List<File>): Boolean {
        val mainFileConfig = readFileAndCreateConfigFile(mainFile)
        val parts = fileParts.map { readFileAndCreateConfigFile(it) }
        val config = Config(mainFile = mainFileConfig, parts = parts)
        val jsonString = Json.encodeToString(config)
        file.createNewFile()
        file.writeText(jsonString)
        return true
    }
    /**
     * Считывает файл и создает для него объект [Config.File] с хешем.
     *
     * @param file Файл для обработки.
     * @return Объект [Config.File].
     */
    private fun readFileAndCreateConfigFile(file: File): Config.File =
        Config.File(name = file.name, hash = fileHasher.sha256sum(file))
}