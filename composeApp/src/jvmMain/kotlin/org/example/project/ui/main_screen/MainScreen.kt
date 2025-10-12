package org.example.project.ui.main_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.SizeUnit
import org.example.project.domain.models.splitSizeToUnits
import org.jetbrains.compose.resources.painterResource
import org.koin.java.KoinJavaComponent.getKoin
import splitkitcat.composeapp.generated.resources.Res
import splitkitcat.composeapp.generated.resources.done

/**
 * Главный экран приложения.
 * Отображает пользовательский интерфейс для разделения и объединения файлов,
 * управляется с помощью [MainViewModel].
 * @see MainViewModel
 * @see MainState
 */
@Composable
internal fun MainScreen() {
    val viewModel = remember { getKoin().get<MainViewModel>() }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (state) {
            MainState.Empty -> {}
            is MainState.Catting -> {

            }

            is MainState.Splitting -> {
                val state = (state as MainState.Splitting)
                Text(
                    "File for splitting:\n" +
                            state.file
                )
                Text(
                    "Size: ${
                        state.file?.size?.let {
                            splitSizeToUnits(it).joinToString(separator = ", ") {
                                "${it.first} ${it.second}"
                            }
                        }
                    }"
                )
                Button(onClick = {
                    viewModel.handleIntent(MainIntent.SelectFileForSplitting)
                }) {
                    Text("select file for splitting")
                }
                Row {
                    OutlinedTextField(
                        value = state.sizeStr,
                        isError = state.sizeIsError,
                        placeholder = { Text("Разбить на тома размером") },
                        onValueChange = {
                            viewModel.handleIntent(MainIntent.SetSizeForSplitting(it))
                        })
                    Box {
                        Text(
                            text = "${state.sizeUnit}",
                            modifier = Modifier
                                .border(BorderStroke(2.dp, Color.Black))
                                .clickable {
                                    viewModel.handleIntent(
                                        MainIntent.SetSizeUnitPeekingMenuVisible(
                                            true
                                        )
                                    )
                                }.padding(10.dp)

                        )
                        DropdownMenu(
                            expanded = state.isSizeUnitPeekingMenuVisible,
                            onDismissRequest = {
                                viewModel.handleIntent(
                                    MainIntent.SetSizeUnitPeekingMenuVisible(false)
                                )
                            }) {
                            SizeUnit.entries.forEach {
                                DropdownMenuItem(text = {
                                    Row {
                                        if (state.sizeUnit == it) {
                                            Icon(
                                                painter = painterResource(Res.drawable.done),
                                                contentDescription = null
                                            )
                                        }
                                        Text("${it.name}")
                                    }
                                }, onClick = {
                                    viewModel
                                        .handleIntent(
                                            MainIntent
                                                .SetSizeUnitForSplitting(it)
                                        )
                                })
                            }

                        }

                    }
                }
                Box(contentAlignment = Alignment.Center) {
                    Button(
                        enabled = state.isSplittingAvailable && !state.isLoading,
                        onClick = { viewModel.handleIntent(MainIntent.Split) }) {
                        Text("Split and write config file")
                    }
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(50.dp))
                    }
                }
            }
        }
    }

}