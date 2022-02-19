package ladislav.sevcuj.endlessdarts

import android.content.Context
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ladislav.sevcuj.endlessdarts.db.Session
import ladislav.sevcuj.endlessdarts.db.SessionStats
import ladislav.sevcuj.endlessdarts.db.User
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreen
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreenData
import ladislav.sevcuj.endlessdarts.ui.screens.game.GameScreenInteractions
import ladislav.sevcuj.endlessdarts.ui.screens.home.HomeScreen
import ladislav.sevcuj.endlessdarts.ui.screens.score.ScoreScreen
import ladislav.sevcuj.endlessdarts.ui.screens.score.ScoreScreenData
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.viewmodels.*
import ladislav.sevcuj.endlessdarts.ui.widgets.NavigationIcon
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal
import java.util.*

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val dataStoreTarget = intPreferencesKey("target")
    private val dataStoreUserId = intPreferencesKey("user_id")
    private val dataStoreUserName = stringPreferencesKey("user_name")
    private val dataStoreUserTemporary = booleanPreferencesKey("user_temporary")

    private lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as App

        val appStateData = runBlocking {
            dataStore.data.first()
        }

        val loggedUser = User(
            id = appStateData[dataStoreUserId]?.toLong() ?: 0,
            identifier = appStateData[dataStoreUserName]?.toString() ?: getRandomUserIdentifier(),
            isTemporary = appStateData[dataStoreUserTemporary] ?: true,
        )

        val defaultTarget = TargetProvider.get(appStateData[dataStoreTarget] ?: TargetProvider.getDefault().number)

        setContent {
            EndlessDartsTheme {
                val startScreen = "home"
                val navController = rememberNavController()
                var route by remember {
                    mutableStateOf(
                        navController.currentDestination?.route ?: startScreen
                    )
                }

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    route = destination.route!!
                }

                val globalViewModel: GlobalViewModel = viewModel(
                    LocalContext.current as ComponentActivity,
                    factory = GlobalViewModelFactory(loggedUser, defaultTarget, app)
                )

                globalViewModel.setUser(loggedUser)

                val target by globalViewModel.target.observeAsState(defaultTarget)
                val user by globalViewModel.user.observeAsState(loggedUser)

                globalViewModel.onUserChange.observe(LocalContext.current as ComponentActivity) { newUser ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        dataStore.edit { data ->
                            data[dataStoreUserId] = newUser.id.toInt()
                            data[dataStoreUserName] = newUser.identifier
                            data[dataStoreUserTemporary] = newUser.isTemporary
                        }
                    }
                }

                globalViewModel.onTargetChange.observe(
                    LocalContext.current as ComponentActivity
                ) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        dataStore.edit { data ->
                            data[dataStoreTarget] = it.number
                        }
                    }
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
                                    navController.navigate("home") {
                                        restoreState = false
                                        launchSingleTop = true
                                    }
                                },
                                imageVector = Icons.Filled.Home,
                                "Home",
                                route == "home"
                            )

                            SpacerHorizontal()
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
                        composable("home") {
                            val homeScreenViewModel: HomeScreeViewModel by viewModels {
                                HomeScreenViewModelFactory(target)
                            }

                            val selectedTarget by homeScreenViewModel.target.observeAsState(target)

                            globalViewModel.onTargetChange.observe(
                                LocalContext.current as ComponentActivity
                            ) {
                                homeScreenViewModel.onTargetSelect(it)
                            }

                            HomeScreen(
                                user = user,
                                onNewUser = {
                                    globalViewModel.setUser(User(
                                        id = 0,
                                        identifier = getRandomUserIdentifier(),
                                        true
                                    ))
                                },
                                target = selectedTarget,
                                onTargetSelect = homeScreenViewModel::onTargetSelect,
                                onStart = {
                                    globalViewModel.changeTarget(homeScreenViewModel.getTarget()) {
                                        navController.navigate("game") {
                                            popUpTo("game")
                                            restoreState = false
                                            launchSingleTop = true
                                        }
                                    }

                                }
                            )
                        }

                        composable("login") {

                        }

                        composable(
                            "game",
                        ) {
                            val gameScreenViewModel: GameScreenViewModel by viewModels {
                                GameScreenViewModelFactory(
                                    app = app,
                                    user = user,
                                    globalViewModel = globalViewModel
                                )
                            }

                            globalViewModel.user.observe(
                                LocalContext.current as ComponentActivity
                            ) {
                                gameScreenViewModel.onUser(it)
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
                                        gameTarget = target,
                                        targetFields = target.prefferedFields,
                                        stats = stats!!.toSimpleData(),
                                        multiplicator = multiplicator,
                                    ),
                                    GameScreenInteractions(
                                        onDart = gameScreenViewModel::onDart,
                                        onActionButton = gameScreenViewModel::onActionButton
                                    ),
                                    onTargetChange = {
                                        globalViewModel.changeTarget(
                                            if (it.id == TargetProvider.randomId) {
                                                TargetProvider.random().copy(label = it.number.toString())
                                            } else {
                                                it
                                            }
                                        ) {
                                            navController.navigate("game") {
                                                popUpTo("game")
                                                restoreState = false
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        composable("score") {
                            val scoreScreenViewModel: ScoreScreenViewModel by viewModels {
                                ScoreScreenViewModelFactory(app, user)
                            }

                            globalViewModel.onSessionCreated.observe(LocalContext.current as ComponentActivity) {
                                scoreScreenViewModel.load(it)
                            }

                            globalViewModel.user.observe(
                                LocalContext.current as ComponentActivity
                            ) {
                                scoreScreenViewModel.onUser(it)
                            }

                            val now = DateInstance.now()

                            val prev = DateInstance.now()
                            prev.add(Calendar.DAY_OF_MONTH, -1)

                            val session by scoreScreenViewModel.selectedSession.observeAsState(
                                initial = Session(
                                    0,
                                    user.id,
                                    now.asDatetimeString(),
                                )
                            )
                            val prevSession by scoreScreenViewModel.prevSession.observeAsState(prev.asDateString())
                            val nextSession by scoreScreenViewModel.nextSession.observeAsState()
                            val throws by scoreScreenViewModel.throws.observeAsState(listOf())
                            val stats by scoreScreenViewModel.stats.observeAsState(SessionStats(0).toFullData())

                            val data = ScoreScreenData(
                                activeSessionDate = DateInstance.fromString(session.startDateTime)
                                    .asDateString(),
                                prevSessionDate = prevSession,
                                nextSessionDate = nextSession,
                                throws = throws,
                                stats = stats,
                            )

                            ScoreScreen(
                                data = data,
                                onSessionSelect = {
                                    scoreScreenViewModel.loadForDate(it)
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}