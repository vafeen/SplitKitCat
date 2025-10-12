package org.example.project.domain.models

internal data class File(
    val name: String,
    val pathToFile: String,
) {
    val fileWithPath: String
        get() = "$pathToFile\\$name"
}