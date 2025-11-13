package org.example.project.domain.services

import java.io.File

interface FileHasher {
    /**
     * Вычисляет контрольную сумму SHA-256 для указанного файла.
     *
     * @param file Файл, для которого необходимо вычислить контрольную сумму.
     * @return Строковое представление контрольной суммы SHA-256 в шестнадцатеричном формате.
     */
    fun sha256sum(file: File): String
}