package ladislav.sevcuj.endlessdarts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ladislav.sevcuj.endlessdarts.db.SessionStats
import ladislav.sevcuj.endlessdarts.db.User
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreen
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreenData
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreenInteractions
import ladislav.sevcuj.endlessdarts.ui.screens.score.ScoreScreen
import ladislav.sevcuj.endlessdarts.ui.screens.score.ScoreScreenData
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.viewmodels.*
import ladislav.sevcuj.endlessdarts.ui.widgets.NavigationIcon
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    private lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as App

        setContent {
            EndlessDartsTheme {
                val startScreen = "game"
                val navController = rememberNavController()
                var route by remember {
                    mutableStateOf(
                        navController.currentDestination?.route ?: startScreen
                    )
                }

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    route = destination.route!!
                }

                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        color = Color.LightGray,
                    ) {
                        Column(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .padding(top = 16.dp),
                        ) {
                            NavigationIcon(
                                onClick = {
                                    navController.navigate("game") {
                                        restoreState = false
                                        launchSingleTop = true
                                    }
                                },
                                imageVector = Icons.Filled.Adjust,
                                "Game",
                                route == "game"
                            )

                            SpacerHorizontal()

                            NavigationIcon(
                                onClick = {
                                    navController.navigate("score") {
                                        restoreState = false
                                        launchSingleTop = true
                                    }
                                },
                                imageVector = Icons.Filled.Analytics,
                                "Score",
                                route == "score"
                            )
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startScreen,
                        modifier = Modifier.weight(1f),
                    ) {
                        composable("login") {

                        }

                        composable("game") {
                            val globalViewModel: GlobalViewModel = viewModel(
                                LocalContext.current as ComponentActivity
                            )

                            val target = ladislav.sevcuj.endlessdarts.db.Target(
                                id = 1,
                                label = "20",
                                number = 20,
                            )

                            val user = User(
                                id = 1,
                                identifier = "User 1",
                                isTemporary = false,
                            )

                            val gameScreenViewModel: GameScreenViewModel by viewModels {
                                GameScreenViewModelFactory(
                                    app = app,
                                    user = user,
                                    target = target,
                                    globalViewModel = globalViewModel
                                )
                            }

                            val currentThrow by gameScreenViewModel.currentThrow.observeAsState()
                            val lastThrow by gameScreenViewModel.lastThrow.observeAsState()
                            val multiplicator by gameScreenViewModel.multiplicator.observeAsState(1)
                            val stats by gameScreenViewModel.stats.observeAsState()

                            if (currentThrow != null
                                && stats != null
                            ) {
                                GameScreen(
                                    GameScreenData(
                                        currentThrow = currentThrow!!,
                                        lastThrow = lastThrow,
                                        target = gameScreenViewModel.target,
                                        targetFields = gameScreenViewModel.target.getPreferredFields(),
                                        stats = stats!!.toSimpleData(),
                                        multiplicator = multiplicator,
                                    ),
                                    GameScreenInteractions(
                                        onDart = gameScreenViewModel::onDart,
                                        onActionButton = gameScreenViewModel::onActionButton
                                    )
                                )
                            }
                        }

                        composable("score") {
                            val globalViewModel: GlobalViewModel = viewModel(
                                LocalContext.current as ComponentActivity
                            )

                            val user = User(
                                id = 1,
                                identifier = "User 1",
                                isTemporary = false,
                            )

                            val scoreScreenViewModel: ScoreScreenViewModel by viewModels {
                                ScoreScreenViewModelFactory(app, user)
                            }

                            globalViewModel.onSessionCreated.observe(LocalContext.current as ComponentActivity) {
                                scoreScreenViewModel.load(it)
                            }

                            val throws by scoreScreenViewModel.throws.observeAsState(listOf())
                            val stats by scoreScreenViewModel.stats.observeAsState(SessionStats(0).toFullData())

                            val data = ScoreScreenData(
                                throws = throws,
                                stats = stats
                            )

                            ScoreScreen(
                                data = data,
                            )
                        }

                    }
                }
            }
        }
    }
}