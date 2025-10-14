package org.example.project.ui.main_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.Config
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

        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                enabled = state !is MainState.Catting,
                onClick = { viewModel.handleIntent(MainIntent.SelectCatting) }) {
                Text("catting")
            }
            Button(
                enabled = state !is MainState.Splitting,
                onClick = { viewModel.handleIntent(MainIntent.SelectSplitting) }) {
                Text("splitting")
            }

        }
        when (val s = state) {
            MainState.Empty -> {}
            is MainState.Catting -> Catting(
                s,
                viewModel::handleIntent
            )

            is MainState.Splitting -> Splitting(
                s,
                viewModel::handleIntent
            )

        }
    }

}

/**
 * Composable-функция для отображения экрана объединения файлов.
 *
 * @param state Текущее состояние экрана объединения.
 * @param sendIntent Функция для отправки намерений в [MainViewModel].
 */
@Composable
internal fun Catting(
    state: MainState.Catting,
    sendIntent: (MainIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Config:\n"
        )
        state.config?.let { config ->
            Button(onClick = { sendIntent(MainIntent.CheckFilesInConfig) }) {
                Text("check files in config")
            }
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().alpha(if (state.isLoading) 0.5f else 1.0f)
                ) {
                    item {
                        File(config.mainFile)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    items(config.parts) {
                        File(
                            it,
                            found = state.foundParts[it.hash] == true,
                            hashIsTrue = state.hashIsTrue[it.hash] == true
                        )
                    }
                }
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }
            }

        }
        Button(onClick = { sendIntent(MainIntent.SelectConfigForCatting) }) {
            Text("select config")
        }
        Button(
            enabled = state.config != null && !state.foundParts.values.contains(false) && !state.isLoading,
            onClick = { sendIntent(MainIntent.Cat) }) {
            Text("cat files")
        }
    }
}

/**
 * Composable-функция для отображения информации о файле.
 *
 * @param file Информация о файле.
 * @param found `true`, если файл найден, `false`, если нет, и `null`, если проверка не проводилась.
 * @param hashIsTrue `true`, если хеш файла верен, `false`, если нет, и `null`, если проверка не проводилась.
 */
@Composable
internal fun File(file: Config.File, found: Boolean? = null, hashIsTrue: Boolean? = null) {
    Row(
        modifier = Modifier
            .border(BorderStroke(2.dp, Color.Black))
            .padding(10.dp)
    ) {
        Column {
            if (found != null) {
                when (found) {
                    true -> "Найден" to Color.Green
                    false -> "Не найден" to Color.Red
                }.let {
                    Text(
                        modifier = Modifier
                            .border(BorderStroke(2.dp, it.second))
                            .padding(5.dp),
                        text = it.first
                    )
                }
            }
            when {
                found == false -> "hash отсутствует" to Color.Black
                hashIsTrue == true -> "hash верен" to Color.Green
                hashIsTrue == false -> "hash неверен" to Color.Red
                else -> null
            }?.let {
                Text(
                    modifier = Modifier
                        .border(BorderStroke(2.dp, it.second))
                        .padding(5.dp),
                    text = it.first
                )
            }
        }
        Column {
            Text(file.name)
            Text(file.hash)
        }
    }
}

/**
 * Composable-функция для отображения экрана разделения файлов.
 *
 * @param state Текущее состояние экрана разделения.
 * @param sendIntent Функция для отправки намерений в [MainViewModel].
 */
@Composable
internal fun Splitting(
    state: MainState.Splitting,
    sendIntent: (MainIntent) -> Unit,
) {
    Text(
        "File for splitting:\n" +
                state.fileForSplitting
    )
    Text(
        "Size: ${
            state.fileForSplitting?.size?.let { size ->
                splitSizeToUnits(size).joinToString(separator = ", ") {
                    "${it.first} ${it.second}"
                }
            }
        }"
    )
    Button(onClick = {
        sendIntent(MainIntent.SelectFileForSplitting)
    }) {
        Text("select file for splitting")
    }
    Row {
        OutlinedTextField(
            value = state.sizeStr,
            isError = state.sizeIsError,
            placeholder = { Text("Разбить на тома размером") },
            onValueChange = {
                sendIntent(MainIntent.SetSizeForSplitting(it))
            })
        Box {
            Text(
                text = "${state.sizeUnit}",
                modifier = Modifier
                    .border(BorderStroke(2.dp, Color.Black))
                    .clickable {
                        sendIntent(
                            MainIntent.SetSizeUnitPeekingMenuVisible(
                                true
                            )
                        )
                    }.padding(10.dp)

            )
            DropdownMenu(
                expanded = state.isSizeUnitPeekingMenuVisible,
                onDismissRequest = {
                    sendIntent(
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
                            Text(it.name)
                        }
                    }, onClick = {
                        sendIntent(
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
            onClick = { sendIntent(MainIntent.Split) }) {
            Text("Split and write config file")
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    }
}