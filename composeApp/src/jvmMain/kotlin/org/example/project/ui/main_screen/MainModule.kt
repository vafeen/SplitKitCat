package org.example.project.ui.main_screen

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


internal val MainScreenModule = module {
    factoryOf(::MainViewModel)
}