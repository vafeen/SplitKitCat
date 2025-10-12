package org.example.project.data

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import org.example.project.domain.models.File
import org.example.project.domain.services.FilePeeker

/**
 * Реализация интерфейса [FilePeeker] для выбора файлов с использованием системного диалога.
 * @see FilePeeker
 */
internal class FilePeekerImpl : FilePeeker {
    /**
     * Открывает системный диалог для выбора одного или нескольких файлов с расширением .kt.
     *
     * @return Список объектов [File], представляющих выбранные файлы, или null, если выбор был отменен.
     */
    override suspend fun peekFiles(): List<File>? {
        val files = FileKit.openFilePicker(
            mode = FileKitMode.Multiple(),
            type = FileKitType.File(extensions = setOf("kt"))
        )
        return files?.map {
            File(name = it.name, pathToFile = it.file.parent)
        }
    }
}