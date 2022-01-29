package ladislav.sevcuj.endlessdarts.ui.screens.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.DartBoard
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.db.Target
import ladislav.sevcuj.endlessdarts.db.Throw
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerVertical
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData

@ExperimentalFoundationApi
@Composable
fun GameScreen(
    data: GameScreenData,
    interactions: GameScreenInteractions,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Ui.padding.dp)
            .verticalScroll(ScrollState(0)),
    ) {
        SpacerHorizontal()

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            GameTarget(
                data.target,
                modifier = Modifier.width(150.dp),
            )

            SpacerVertical(Ui.padding * 2)

            GameDashboard(
                data.currentThrow,
                data.lastThrow,
                modifier = Modifier.weight(1f),
            )

            SpacerVertical(Ui.padding * 2)

            GameStats(
                data.stats,
                modifier = Modifier.weight(1f),
            )
        }

        SpacerHorizontal(Ui.padding * 2)

        GameKeyboard(
            data.targetFields,
            onDart = interactions.onDart,
            modifier = Modifier.fillMaxWidth(),
        )

        SpacerHorizontal()
    }
}

data class GameScreenData(
    val target: Target,
    val targetFields: List<DartBoard.Field>,
    val currentThrow: Throw,
    val lastThrow: Throw?,
    val stats: List<StatsRowData>,
)


data class GameScreenInteractions(
    val onDart: (DartBoard.Field) -> Unit,
)
