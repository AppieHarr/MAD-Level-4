package com.example.mad.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.mad.R
import com.example.mad.Utils
import com.example.mad.data.Game
import com.example.mad.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, viewModel: GameViewModel) {
    val context = LocalContext.current
    val games by viewModel.gameBacklog.observeAsState(emptyList())
    val scaffoldState = rememberScaffoldState() // Needed for the Snackbar object.
    val scope = rememberCoroutineScope() // Also needed for the Snackbar object.
    var deletedBacklog by remember { mutableStateOf(false) } // Switch to decide whether Snackbar "undo delete all" option is used.

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.DarkGray,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.app_name))
                        IconButton(onClick = {
                            // Game backlog should be cleared from the database  only after confirmation i.e."UNDO" option NOT
                            // selected on the "Snackbar". For that purpose we have introduced this switch.
                            deletedBacklog = true
                            scope.launch {
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.snackbar_msg),
                                    actionLabel = context.getString(R.string.undo)
                                )
                                if (result != SnackbarResult.ActionPerformed) {
                                    viewModel.deleteGameBacklog()
                                }
                                deletedBacklog = false
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete all games"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddGameScreen.route) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) {
        if (!deletedBacklog) {
            Games(
                context = context,
                games,
                modifier = Modifier.padding(16.dp),
                scaffoldState
            )
        }
    }
}


@Composable
fun Games(
    context: Context,
    games: List<Game>,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState
) {
    LazyColumn(modifier = modifier) {
        items(games) { game ->
            GameCard(game = game, context = context, scaffoldState = scaffoldState)
        }
    }
}