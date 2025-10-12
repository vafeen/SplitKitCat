package org.example.project

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.data.di.KoinDataModule
import org.example.project.domain.services.FileSplitter
import org.example.project.ui.App
import org.example.project.ui.main_screen.MainScreenModule
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

/**
 * Главная точка входа в приложение.
 */
fun main() {
    startKoin {
        modules(
            KoinDataModule,
            MainScreenModule
        )
    }
    application {
        LaunchedEffect(null) {
            val fileSplitter: FileSplitter = getKoin().get()
//            fileProcessor.sha256sum("")
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = "KotlinProject",
        ) {
            App()
        }
    }
}