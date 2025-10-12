package org.example.project.domain.services

import org.example.project.domain.models.Config

internal interface ConfigHandler {
    suspend fun readConfig(): Config?
    suspend fun writeConfig(
        mainFileName: String,
        fileParts: List<String>,
    ): Boolean
}