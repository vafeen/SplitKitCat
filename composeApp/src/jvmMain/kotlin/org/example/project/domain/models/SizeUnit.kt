package org.example.project.domain.models

internal enum class SizeUnit {
    Bytes,
    KiloBytes,
    Megabytes,
    Gigabytes;

    /**
     * Преобразует заданное значение из этой единицы измерения в байты.
     *
     * @param value Значение для преобразования.
     * @return Значение в байтах.
     */
    fun toBytes(value: Int): Int = when (this) {
        Bytes -> value
        KiloBytes -> value * 1024
        Megabytes -> value * 1024 * 1024
        Gigabytes -> value * 1024 * 1024 * 1024
    }
}

internal fun splitSizeToUnits(sizeInBytes: Long): List<Pair<Long, SizeUnit>> {
    var remainder = sizeInBytes
    val gigabytes = remainder / (1024 * 1024 * 1024)
    remainder %= (1024 * 1024 * 1024)

    val megabytes = remainder / (1024 * 1024)
    remainder %= (1024 * 1024)

    val kilobytes = remainder / 1024
    remainder %= 1024

    val bytes = remainder

    return listOf(
        Pair(gigabytes, SizeUnit.Gigabytes),
        Pair(megabytes, SizeUnit.Megabytes),
        Pair(kilobytes, SizeUnit.KiloBytes),
        Pair(bytes, SizeUnit.Bytes)
    )
}

