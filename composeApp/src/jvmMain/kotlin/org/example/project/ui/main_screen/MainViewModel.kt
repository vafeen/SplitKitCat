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

internal class MainViewModel(
    private val fileSplitter: FileSplitter,
    private val filePeeker: FilePeeker,
    private val configHandler: ConfigHandler
) : ViewModel() {
    private val _state = MutableStateFlow<MainState>(MainState.Splitting())
    val state = _state.asStateFlow()
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

    private suspend fun selectFileForSplitting() {
        val file = filePeeker.peekFileForSplitting() ?: return
        _state.update {
            (it as MainState.Splitting).copy(file = file)
        }
    }

    private fun setSizeUnitPeekingMenuVisible(isVisible: Boolean) = _state.update {
        (it as MainState.Splitting).copy(isSizeUnitPeekingMenuVisible = isVisible)
    }

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

    private fun setSizeUnitForSplitting(sizeUnit: SizeUnit) = _state.update {
        (it as MainState.Splitting).copy(
            sizeUnit = sizeUnit,
            isSizeUnitPeekingMenuVisible = false
        )
    }

}