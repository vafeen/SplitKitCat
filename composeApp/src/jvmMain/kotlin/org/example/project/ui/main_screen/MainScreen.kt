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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.java.KoinJavaComponent.getKoin


@Composable
internal fun MainScreen() {
    val viewModel = remember { getKoin().get<MainViewModel>() }
    val files by viewModel.files.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            viewModel.showFilePicker()
        }) {
            Text("Выбрать файл")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Выбранный файл: ${files.joinToString()}")
    }

}