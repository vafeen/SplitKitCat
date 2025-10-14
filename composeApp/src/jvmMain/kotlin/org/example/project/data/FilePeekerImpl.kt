package org.example.project.data

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.size
import org.example.project.domain.models.Config
import org.example.project.domain.models.FileInfo
import org.example.project.domain.services.FilePeeker

/**
 * Реализация интерфейса [FilePeeker] для выбора файлов с использованием системного диалога.
 * @see FilePeeker
 */
internal class FilePeekerImpl : FilePeeker {
    /**
     * Открывает системный диалог для выбора одного или нескольких файлов с расширением .kt.
     *
     * @return Список объектов [FileInfo], представляющих выбранные файлы, или null, если выбор был отменен.
     */
    override suspend fun peekFile(): FileInfo? = FileKit.openFilePicker(
        mode = FileKitMode.Single,
    )?.let {
        FileInfo(name = it.name, file = it.file, size = it.size())
    }

    override suspend fun peekConfig(): FileInfo? = FileKit.openFilePicker(
        mode = FileKitMode.Single,
        type = FileKitType.File(extension = Config.Extension)
    )?.let {
        FileInfo(name = it.name, file = it.file, size = it.size())
    }

    override suspend fun peekFileForSaving(
        suggestedName: String,
        extension: String?,
    ): FileInfo? = FileKit.openFileSaver(
        suggestedName = suggestedName,
        extension = extension,
    )?.let {
        FileInfo(name = it.name, file = it.file, size = it.size())
    }
}