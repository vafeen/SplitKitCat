package org.example.project.data

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.domain.models.Config
import org.example.project.domain.services.ConfigHandler
import org.example.project.domain.services.FileSplitter
import java.io.File

internal class ConfigHandlerImpl(
    private val fileSplitter: FileSplitter,
) : ConfigHandler {
    override suspend fun readConfig(): Config? {
        val configFile = FileKit.openFilePicker(
            mode = FileKitMode.Single,
            type = FileKitType.File(extensions = setOf(Config.Extension))
        ) ?: return null
        val content = configFile.file.readText()
        return runCatching {
            Json.decodeFromString<Config>(content)
        }.getOrNull()
    }

    override suspend fun writeConfig(
        mainFileName: String,
        fileParts: List<String>,
    ): Boolean {

        val fileToSaveConfig = FileKit.openFileSaver(
            suggestedName = "${File(mainFileName).nameWithoutExtension}.${Config.Extension}",
            extension = Config.Extension
        )?.file ?: return false
        val mainFile = readFileAndCreateConfigFile(mainFileName)
        println()
        val parts = fileParts.map { readFileAndCreateConfigFile(it) }
        val config = Config(mainFile = mainFile, parts = parts)
        val jsonString = Json.encodeToString(config)
        fileToSaveConfig.writeText(jsonString)
        return true
    }

    private fun readFileAndCreateConfigFile(fileName: String): Config.File {
        val hash = fileSplitter.sha256sum(fileName)
        return Config.File(name = File(fileName).name, hash = hash)
    }
}