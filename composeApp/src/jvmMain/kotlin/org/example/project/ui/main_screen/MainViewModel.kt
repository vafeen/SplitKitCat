package org.example.project.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.models.File
import org.example.project.domain.services.FilePeeker
import org.example.project.domain.services.FileSplitter

internal class MainViewModel(
    private val fileSplitter: FileSplitter,
    private val filePeeker: FilePeeker,
) : ViewModel() {
    private val _files = MutableStateFlow(listOf<File>())
    val files = _files.asStateFlow()
    fun showFilePicker() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = filePeeker.peekFiles()
            files?.let { _files.emit(it) }
        }
    }

}