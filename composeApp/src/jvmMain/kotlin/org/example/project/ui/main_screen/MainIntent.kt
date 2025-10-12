package org.example.project.ui.main_screen

import org.example.project.domain.models.SizeUnit

internal sealed interface MainIntent {
    data class SetSizeForSplitting(val sizeStr: String) : MainIntent
    data class SetSizeUnitForSplitting(val sizeUnit: SizeUnit) : MainIntent
    data class SetSizeUnitPeekingMenuVisible(val isVisible: Boolean) : MainIntent
    data object SelectFileForSplitting : MainIntent
    data object Split : MainIntent
}