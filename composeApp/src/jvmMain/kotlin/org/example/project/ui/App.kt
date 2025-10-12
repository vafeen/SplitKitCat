package org.example.project.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.project.ui.main_screen.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

//import splitkitcat.composeapp.generated.resources.Res
//import splitkitcat.composeapp.generated.resources.compose_multiplatform

/**
 * Главный Composable-компонент приложения.
 * Устанавливает [MaterialTheme] и отображает [MainScreen].
 */
@Composable
@Preview
fun App() {
    MaterialTheme { MainScreen() }
}