package org.example.project.ui.main_screen

import org.example.project.domain.models.SizeUnit

/**
 * Представляет намерения пользователя на главном экране.
 */
internal sealed interface MainIntent {
    data object SelectCatting : MainIntent
    data object SelectSplitting : MainIntent
    data object SelectConfigForCatting : MainIntent

    /**
     * Намерение установить размер для разделения файла.
     * @property sizeStr Строка с размером.
     */
    data class SetSizeForSplitting(val sizeStr: String) : MainIntent

    /**
     * Намерение установить единицу измерения размера для разделения.
     * @property sizeUnit Единица измерения.
     */
    data class SetSizeUnitForSplitting(val sizeUnit: SizeUnit) : MainIntent

    /**
     * Намерение установить видимость меню выбора единиц измерения.
     * @property isVisible `true`, если меню должно быть видимо.
     */
    data class SetSizeUnitPeekingMenuVisible(val isVisible: Boolean) : MainIntent

    /**
     * Намерение выбрать файл для разделения.
     */
    data object SelectFileForSplitting : MainIntent

    /**
     * Намерение запустить процесс разделения файла.
     */
    data object Split : MainIntent
    data object Cat : MainIntent
    data object CheckFilesInConfig : MainIntent
}