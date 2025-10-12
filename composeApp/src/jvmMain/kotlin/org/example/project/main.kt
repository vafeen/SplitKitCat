package org.example.project

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.data.di.KoinDataModule
import org.example.project.domain.FileProcessor
import org.example.project.ui.App
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

//fun main() {
//    splitFile("./files/KotlinProject.zip", outputDirPath = "./files",50*1024)
//    catFiles("./files", "./files", "KotlinProject.zip")
//}

fun main() {
    startKoin {
        modules(KoinDataModule)
    }
    application {
        LaunchedEffect(null) {
            val fileProcessor: FileProcessor = getKoin().get()
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