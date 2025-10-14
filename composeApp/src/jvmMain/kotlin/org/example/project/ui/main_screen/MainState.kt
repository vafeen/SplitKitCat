package org.example.project.ui.main_screen

import org.example.project.domain.models.Config
import org.example.project.domain.models.FileInfo
import org.example.project.domain.models.SizeUnit
import java.io.File

/**
 * Представляет состояние главного экрана.
 */
internal sealed class MainState {
    /**
     * Пустое состояние, когда ничего не выбрано.
     */
    data object Empty : MainState()

    /**
     * Состояние для разделения файла.
     *
     * @property fileForSplitting Выбранный для разделения файл.
     * @property sizeStr Строковое представление размера части.
     * @property size Размер части в виде числа.
     * @property sizeIsError true, если введенный размер некорректен.
     * @property sizeUnit Единица измерения размера части.
     * @property isSizeUnitPeekingMenuVisible true, если видимо меню выбора единиц измерения.
     * @property isLoading true, если идет процесс разделения.
     * @property isSplittingAvailable true, если можно начать разделение.
     */
    data class Splitting(
        val fileForSplitting: FileInfo? = null,
        val sizeStr: String = "",
        val size: Int? = null,
        val sizeIsError: Boolean = false,
        val sizeUnit: SizeUnit = SizeUnit.Bytes,
        val isSizeUnitPeekingMenuVisible: Boolean = false,
        val isLoading: Boolean = false,
    ) : MainState() {
        val isSplittingAvailable: Boolean
            get() = fileForSplitting != null && size != null
    }

    /**
     * Состояние для объединения файлов.
     *
     * @property config Конфигурация для объединения.
     */
    data class Catting(
        val config: Config? = null,
        val configFile: File? = null,
        val foundPartsHash: Map<String, Boolean> = mapOf()
    ) : MainState()
}
