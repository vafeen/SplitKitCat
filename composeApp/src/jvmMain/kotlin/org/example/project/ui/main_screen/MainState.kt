package org.example.project.ui.main_screen

import org.example.project.domain.models.Config
import org.example.project.domain.models.FileInfo
import org.example.project.domain.models.SizeUnit

internal sealed class MainState(
) {
    data object Empty : MainState()
    data class Splitting(
        val file: FileInfo? = null,
        val sizeStr: String = "",
        val size: Int? = null,
        val sizeIsError: Boolean = false,
        val sizeUnit: SizeUnit = SizeUnit.Bytes,
        val isSizeUnitPeekingMenuVisible: Boolean = false,
        val isLoading: Boolean = false,
    ) : MainState() {
        val isSplittingAvailable: Boolean
            get() = file != null && size != null
    }

    data class Catting(
        val config: Config?
    ) : MainState()
}
