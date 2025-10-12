package org.example.project.ui.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.java.KoinJavaComponent.getKoin
import java.awt.FileDialog
import java.awt.Frame


@Composable
internal fun MainScreen() {
    val vm = remember { getKoin().get<MainViewModel>() }
    var selectedFilePath by remember { mutableStateOf<String?>(null) }

    fun openFilePicker() {
        val fileDialog = FileDialog(Frame(), "Choose a file", FileDialog.LOAD)
        fileDialog.isVisible = true
        val file = fileDialog.file
        val dir = fileDialog.directory
        if (file != null && dir != null) {
            selectedFilePath = "$dir==$file"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { openFilePicker() }) {
            Text("Выбрать файл")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Выбранный файл: ${selectedFilePath ?: "файл не выбран"}")
    }

}