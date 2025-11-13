package org.example.project.data

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import org.example.project.domain.models.Config
import org.example.project.domain.services.FilePeeker
import java.io.File

/**
 * Реализация интерфейса [FilePeeker] для выбора файлов с использованием системного диалога.
 * @see FilePeeker
 */
internal class FilePeekerImpl : FilePeeker {
    /**
     * Открывает системный диалог для выбора одного файла.
     *
     * @return Объект [FileInfo], представляющий выбранный файл, или null, если выбор был отменен.
     */
    override suspend fun peekFile(): File? = FileKit.openFilePicker(
        mode = FileKitMode.Single,
    )?.file

    /**
     * Открывает системный диалог для выбора файла конфигурации.
     *
     * @return Объект [FileInfo], представляющий выбранный файл конфигурации, или null, если выбор был отменен.
     */
    override suspend fun peekConfig(): File? = FileKit.openFilePicker(
        mode = FileKitMode.Single,
        type = FileKitType.File(extension = Config.Extension)
    )?.file

    /**
     * Открывает системный диалог для сохранения файла.
     *
     * @param suggestedName Предлагаемое имя для сохраняемого файла.
     * @param extension Предлагаемое расширение для сохраняемого файла.
     * @return Объект [FileInfo], представляющий место для сохранения файла, или null, если выбор был отменен.
     */
    override suspend fun peekFileForSaving(
        suggestedName: String,
        extension: String?,
    ): File? = FileKit.openFileSaver(
        suggestedName = suggestedName,
        extension = extension,
    )?.file
}