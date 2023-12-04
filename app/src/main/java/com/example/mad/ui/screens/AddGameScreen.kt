package com.example.mad.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings.Global.getString
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mad.R
import com.example.mad.Utils
import com.example.mad.data.Game
import com.example.mad.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddGameScreen(navController: NavController, viewModel: GameViewModel) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Scaffold(
        backgroundColor = Color.DarkGray,
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Game") },
                // "Arrow back" implementation.
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to homescreen"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val newGame = createGame(context, title, platform, day, month, year)
                if (newGame.title.isNotEmpty()) {
                    viewModel.insertGame(newGame)
                    navController.popBackStack()
                }
            }) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Save")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Title",
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth()
                    )
                    CustomTextField(
                        value = platform,
                        onValueChange = { platform = it },
                        label = "Platform",
                        keyboardType = KeyboardType.Text,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CustomTextField(
                            value = day,
                            onValueChange = { if (day.length <= 2) day = it },
                            label = "Day",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(0.33f)
                        )
                        CustomTextField(
                            value = month,
                            onValueChange = { if (month.length <= 2) month = it },
                            label = "Month",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(0.33f)
                        )
                        CustomTextField(
                            value = year,
                            onValueChange = { if (year.length <= 4) year = it },
                            label = "Year",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(0.33f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

private fun createGame(context: Context, title: String, platform: String,
                       day: String, month: String, year: String): Game {
    val errorMessage = context.getString(R.string.empty_title)
    if (title.isBlank()) {
        throw IllegalArgumentException(errorMessage)
    }

    val dayInt = day.toIntOrNull()
    val monthInt = month.toIntOrNull()
    val yearInt = year.toIntOrNull()

    if (dayInt == null || monthInt == null || yearInt == null) {
        throw IllegalArgumentException(context.getString(R.string.wrong_date))
    }

    val calendar = Calendar.getInstance()
    calendar.isLenient = false
    calendar.set(yearInt, monthInt - 1, dayInt)

    val date = calendar.time

    return Game(
        title = title,
        platform = platform,
        release = date
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameCard(context: Context, game: Game, viewModel: GameViewModel = viewModel(), scaffoldState: ScaffoldState) {
    val dismissState = rememberDismissState()
    if (dismissState.isDismissed(DismissDirection.StartToEnd) || dismissState.isDismissed(DismissDirection.EndToStart)) {
        LaunchedEffect(Unit) {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = context.getString(R.string.deleted_game, game.title),
                actionLabel = context.getString(R.string.undo)
            )
            if (result != SnackbarResult.ActionPerformed) {
                viewModel.deleteGame(game)
            } else {
                dismissState.reset()
            }
        }
    }

    SwipeToDismiss(
        state = dismissState,
        background = {},
        dismissContent = {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = game.title, style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = game.platform)
                        Text(text = "Release: " + Utils.dateToString(game.release))
                    }
                }
            }
        },
        directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
    )
}



