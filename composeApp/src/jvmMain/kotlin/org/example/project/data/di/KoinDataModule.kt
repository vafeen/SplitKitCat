package org.example.project.data.di

import org.example.project.data.ConfigHandlerImpl
import org.example.project.data.FilePeekerImpl
import org.example.project.data.FileSplitterImpl
import org.example.project.domain.services.ConfigHandler
import org.example.project.domain.services.FilePeeker
import org.example.project.domain.services.FileSplitter
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


internal val KoinDataModule = module {
    factoryOf(::FileSplitterImpl) {
        bind<FileSplitter>()
    }
    factoryOf(::FilePeekerImpl) {
        bind<FilePeeker>()
    }
    factoryOf(::ConfigHandlerImpl) {
        bind<ConfigHandler>()
    }
}