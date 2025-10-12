package org.example.project.domain.models

internal data class FileInfo(
    val name: String,
    val pathToFile: String,
    val size: Long,
) {
    val fileWithPath: String
        get() = "$pathToFile\\$name"
}