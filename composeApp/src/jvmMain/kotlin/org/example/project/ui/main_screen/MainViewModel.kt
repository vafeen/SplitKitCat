package org.example.project.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.Config
import org.example.project.domain.models.SizeUnit
import org.example.project.domain.services.ConfigHandler
import org.example.project.domain.services.FileHasher
import org.example.project.domain.services.FilePeeker
import org.example.project.domain.services.FileSplitter
import java.io.File

/**
 * ViewModel для главного экрана, управляющая состоянием и взаимодействием с пользователем.
 *
 * @property fileSplitter Сервис для разделения и объединения файлов.
 * @property filePeeker Сервис для выбора файлов.
 * @property configHandler Сервис для работы с файлами конфигурации.
 */
internal class MainViewModel(
    private val fileSplitter: FileSplitter,
    private val fileHasher: FileHasher,
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
                MainIntent.SelectCatting -> selectCatting()
                MainIntent.SelectSplitting -> selectSplitting()
                MainIntent.SelectConfigForCatting -> selectConfigForCatting()
                MainIntent.Cat -> cat()
                MainIntent.CheckFilesInConfig -> checkFilesInConfig()
            }
        }
    }

    /**
     * Выполняет объединение файлов на основе выбранной конфигурации.
     */
    private suspend fun cat() {
        var state = _state.value as? MainState.Catting ?: return
        val config = state.config ?: return
        val configFile = state.configFile ?: return
        val mainFile = config.mainFile
        state = state.copy(isLoading = true)
        _state.update { state }
        val outputFile = filePeeker.peekFileForSaving(
            suggestedName = config.mainFile.name,
            extension = File(mainFile.name).extension
        ) ?: return
        delay(5000)
        fileSplitter.catFiles(
            partsDir = File(configFile.parent),
            outputFile = outputFile, baseFileName = mainFile.name
        )
        val hash = fileHasher.sha256sum(outputFile)
        state = state.copy(isMainFileHashTrue = hash == config.mainFile.hash)
        _state.update { state }
        state = state.copy(isLoading = false)
        _state.update { state }
    }

    /**
     * Открывает диалог выбора файла конфигурации и считывает его.
     */
    private suspend fun selectConfigForCatting() {
        val configFile = filePeeker.peekConfig() ?: return
        val config = configHandler.readConfig(configFile) ?: return // TODO(add showing error)

        _state.update {
            if (it is MainState.Catting) it.copy(
                config = config,
                configFile = configFile
            ) else it
        }
        checkFilesInConfig()
    }

    /**
     * Проверяет наличие и целостность файлов-частей, указанных в конфигурации.
     */
    private suspend fun checkFilesInConfig() {
        var state = _state.value as? MainState.Catting ?: return
        val config = state.config ?: return
        val directory = state.configFile?.parent ?: return

        val parts = config.parts.toMutableList()

        state = state.copy(foundParts = mapOf(), hashIsTrue = mapOf(), isLoading = true)
        _state.update { state }
        parts.forEach { partFile ->
            val fileSystemFile = File(directory, partFile.name)
            val exists = fileSystemFile.exists()
            state = state.copy(
                foundParts = state.foundParts.plus(
                    partFile.hash to exists
                )
            )
            _state.update { state }
//            delay(500)
            state = state.copy(
                hashIsTrue = state.hashIsTrue.plus(
                    partFile.hash to
                            if (exists)
                                partFile.hash == fileHasher.sha256sum(fileSystemFile)
                            else false
                )
            )
            _state.update { state }

        }
//        delay(5000)
        _state.update { state.copy(isLoading = false) }
    }

    /**
     * Переключает состояние на объединение файлов.
     */
    private fun selectCatting() = _state.update {
        MainState.Catting()
    }

    /**
     * Переключает состояние на разделение файлов.
     */
    private fun selectSplitting() = _state.update {
        MainState.Splitting()
    }

    /**
     * Выполняет разделение файла на части на основе текущего состояния.
     */
    private suspend fun split() {
        val state = _state.value as MainState.Splitting
        val fileForSplitting = state.fileForSplitting
        val size = state.size
        val sizeUnit = state.sizeUnit
        if (fileForSplitting != null && size != null) {
            _state.update {
                if (it is MainState.Splitting) it.copy(isLoading = true) else it
            }

            val configFileForSaving = filePeeker.peekFileForSaving(
                suggestedName = fileForSplitting.nameWithoutExtension,
                extension = Config.Extension
            ) ?: return
            val fileParts = fileSplitter.splitFile(
                inputFile = fileForSplitting,
                outputDirPath = configFileForSaving.parent,
                chunkSize = sizeUnit.toBytes(size)
            )

            configHandler.writeConfig(
                file = configFileForSaving,
                mainFile = fileForSplitting,
                fileParts = fileParts,
            )
//            delay(5000)
            _state.update {
                if (it is MainState.Splitting) it.copy(isLoading = false) else it
            }
        }
    }

    /**
     * Открывает диалог выбора файла для разделения и обновляет состояние.
     */
    private suspend fun selectFileForSplitting() {
        val file = filePeeker.peekFile() ?: return
        _state.update {
            if (it is MainState.Splitting) it.copy(fileForSplitting = file) else it
        }
    }

    /**
     * Устанавливает видимость меню выбора единиц измерения размера.
     *
     * @param isVisible true, если меню должно быть видимо, иначе false.
     */
    private fun setSizeUnitPeekingMenuVisible(isVisible: Boolean) = _state.update {
        if (it is MainState.Splitting)
            it.copy(isSizeUnitPeekingMenuVisible = isVisible)
        else it
    }

    /**
     * Устанавливает размер для разделения файла из строки.
     *
     * @param sizeStr Строка с размером.
     */
    private fun setSizeForSplitting(sizeStr: String) {
        _state.update {
            val size = sizeStr.toLongOrNull()
            if (it is MainState.Splitting)
                it.copy(sizeStr = sizeStr, size = size, sizeIsError = size == null)
            else it
        }
    }

    /**
     * Устанавливает единицу измерения размера для разделения файла.
     *
     * @param sizeUnit Единица измерения размера.
     */
    private fun setSizeUnitForSplitting(sizeUnit: SizeUnit) = _state.update {
        if (it is MainState.Splitting)
            it.copy(sizeUnit = sizeUnit, isSizeUnitPeekingMenuVisible = false)
        else it
    }

}