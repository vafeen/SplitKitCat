package org.example.project.domain.models

import kotlinx.serialization.Serializable

/**
 * Представляет конфигурацию для разделенного файла.
 *
 * @property mainFile Информация об основном (исходном) файле.
 * @property parts Список частей, на которые был разделен основной файл.
 */
@Serializable
internal data class Config(
    val mainFile: File,
    val parts: List<File>,
) {
    /**
     * Представляет информацию о файле (основном или части) в конфигурации.
     *
     * @property name Имя файла.
     * @property hash Контрольная сумма SHA-256 файла.
     */
    @Serializable
    data class File(
        val name: String,
        val hash: String,
    )

    /**
     * Компаньон для хранения констант, связанных с конфигурацией.
     */
    companion object {
        /**
         * Расширение файла конфигурации.
         */
        const val Extension = "kit-cat-config"
    }
}