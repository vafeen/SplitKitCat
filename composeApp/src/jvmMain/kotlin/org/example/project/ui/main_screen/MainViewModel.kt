package org.example.project.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.SizeUnit
import org.example.project.domain.services.ConfigHandler
import org.example.project.domain.services.FilePeeker
import org.example.project.domain.services.FileSplitter

/**
 * ViewModel для главного экрана, управляющая состоянием и взаимодействием с пользователем.
 *
 * @property fileSplitter Сервис для разделения файлов.
 * @property filePeeker Сервис для выбора файлов.
 * @property configHandler Сервис для работы с файлами конфигурации.
 */
internal class MainViewModel(
    private val fileSplitter: FileSplitter,
    private val filePeeker: FilePeeker,
    private val configHandler: ConfigHandler
) : ViewModel() {
    private val _state = MutableStateFlow<MainState>(MainState.Splitting())

    /**
     * Поток состояния для главного экрана, за которым следит UI.
     */
    val state = _state.asStateFlow()

    /**
     * Обрабатывает намерения, поступающие от UI.
     *
     * @param intent Намерение для обработки.
     */
    fun handleIntent(intent: MainIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intent) {
                is MainIntent.SetSizeUnitForSplitting -> setSizeUnitForSplitting(intent.sizeUnit)
                is MainIntent.SetSizeForSplitting -> setSizeForSplitting(intent.sizeStr)
                is MainIntent.SetSizeUnitPeekingMenuVisible -> setSizeUnitPeekingMenuVisible(intent.isVisible)
                MainIntent.SelectFileForSplitting -> selectFileForSplitting()
                MainIntent.Split -> split()
            }
        }
    }

    /**
     * Выполняет разделение файла на части на основе текущего состояния.
     */
    private suspend fun split() {
        val state = _state.value as MainState.Splitting
        val file = state.file
        val size = state.size
        val sizeUnit = state.sizeUnit
        if (file != null && size != null) {
            _state.update {
                (it as MainState.Splitting)
                    .copy(isLoading = true)
            }
            val partFileNames = fileSplitter.splitFile(
                file.fileWithPath,
                outputDirPath = file.pathToFile,
                chunkSize = sizeUnit.toBytes(size)
            )
            configHandler.writeConfig(
                mainFileName = file.fileWithPath,
                fileParts = partFileNames,
            )
//            delay(5000)
            _state.update {
                (it as MainState.Splitting)
                    .copy(isLoading = false)
            }
        }
    }

    /**
     * Открывает диалог выбора файла и обновляет состояние выбранным файлом.
     */
    private suspend fun selectFileForSplitting() {
        val file = filePeeker.peekFileForSplitting() ?: return
        _state.update {
            (it as MainState.Splitting).copy(file = file)
        }
    }

    /**
     * Устанавливает видимость меню выбора единиц измерения размера.
     *
     * @param isVisible true, если меню должно быть видимо, иначе false.
     */
    private fun setSizeUnitPeekingMenuVisible(isVisible: Boolean) = _state.update {
        (it as MainState.Splitting).copy(isSizeUnitPeekingMenuVisible = isVisible)
    }

    /**
     * Устанавливает размер для разделения файла из строки.
     *
     * @param sizeStr Строка с размером.
     */
    private fun setSizeForSplitting(sizeStr: String) {
        _state.update {
            val size = sizeStr.toIntOrNull()
            (it as MainState.Splitting).copy(
                sizeStr = sizeStr,
                size = size ?: 0,
                sizeIsError = size == null
            )
        }
    }

    /**
     * Устанавливает единицу измерения размера для разделения файла.
     *
     * @param sizeUnit Единица измерения размера.
     */
    private fun setSizeUnitForSplitting(sizeUnit: SizeUnit) = _state.update {
        (it as MainState.Splitting).copy(
            sizeUnit = sizeUnit,
            isSizeUnitPeekingMenuVisible = false
        )
    }

}