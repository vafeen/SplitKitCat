package org.example.project.data

import org.example.project.domain.services.FileHasher
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

internal class FileHasherImpl : FileHasher {
    /**
     * Вычисляет контрольную сумму SHA-256 для указанного файла.
     *
     * @param file Файл, для которого необходимо вычислить контрольную сумму.
     * @return Строковое представление контрольной суммы SHA-256 в шестнадцатеричном формате.
     * @throws Exception если файл не существует.
     */
    override fun sha256sum(file: File): String {
        if (!file.exists()) throw Exception("File ${file.name} is not exists in FileHasherImpl.sha256sum")
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(1024 * 8)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}