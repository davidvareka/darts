package ladislav.sevcuj.endlessdarts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreen
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreenData
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreenInteractions
import ladislav.sevcuj.endlessdarts.ui.screens.score.ScoreScreen
import ladislav.sevcuj.endlessdarts.ui.screens.score.ScoreScreenData
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.viewmodels.GameScreenViewModel
import ladislav.sevcuj.endlessdarts.ui.viewmodels.GameScreenViewModelFactory
import ladislav.sevcuj.endlessdarts.ui.viewmodels.ScoreScreenViewModel
import ladislav.sevcuj.endlessdarts.ui.viewmodels.ScoreScreenViewModelFactory

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EndlessDartsTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "game") {
                    composable("login") {

                    }

                    composable("game") {
                        val gameScreenViewModel: GameScreenViewModel by viewModels {
                            GameScreenViewModelFactory()
                        }

                        val currentThrow by gameScreenViewModel.currentThrow.observeAsState()
                        val lastThrow by gameScreenViewModel.lastThrow.observeAsState()
                        val multiplicator by gameScreenViewModel.multiplicator.observeAsState(1)
                        val target by gameScreenViewModel.target.observeAsState()
                        val targetFields by gameScreenViewModel.targetFields.observeAsState()
                        val stats by gameScreenViewModel.stats.observeAsState()

                        if (currentThrow != null
                            && target != null
                            && targetFields != null
                            && stats != null
                        ) {
                            GameScreen(
                                GameScreenData(
                                    currentThrow = currentThrow!!,
                                    lastThrow = lastThrow,
                                    target = target!!,
                                    targetFields = targetFields!!,
                                    stats = stats!!,
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
                        val scoreScreenViewModel: ScoreScreenViewModel by viewModels {
                            ScoreScreenViewModelFactory()
                        }

                        val throws by scoreScreenViewModel.throws.observeAsState()
                        val stats by scoreScreenViewModel.stats.observeAsState()

                        if (throws != null && stats != null) {
                            val data = ScoreScreenData(
                                throws = throws!!,
                                stats = stats!!
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