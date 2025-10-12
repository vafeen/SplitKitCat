package org.example.project.data.di

import org.example.project.data.FileProcessorImpl
import org.example.project.domain.FileProcessor
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


internal val KoinDataModule = module {
    factoryOf(::FileProcessorImpl) {
        bind<FileProcessor>()
    }
}