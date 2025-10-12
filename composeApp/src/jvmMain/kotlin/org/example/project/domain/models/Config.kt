package org.example.project.domain.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Config(
    val mainFile: File,
    val parts: List<File>,
) {
    @Serializable
    data class File(
        val name: String,
        val hash: String,
    )

    companion object {
        const val Extension = "kit-cat-config"
    }
}