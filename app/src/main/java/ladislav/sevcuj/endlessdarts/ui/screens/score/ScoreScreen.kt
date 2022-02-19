package ladislav.sevcuj.endlessdarts.ui.screens.score

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerVertical
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData
import ladislav.sevcuj.endlessdarts.ui.widgets.WidgetTitle

@Composable
fun ScoreScreen(
    data: ScoreScreenData,
    onSessionSelect: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Ui.padding.dp)
            .verticalScroll(ScrollState(0)),
    ) {
        SpacerHorizontal()

        WidgetTitle(value = "Game History")

        SpacerHorizontal()

        SessionPicker(
            active = data.activeSessionDate,
            prev = data.prevSessionDate,
            next = data.nextSessionDate ?: "",
            onSelect = onSessionSelect,
            modifier = Modifier.fillMaxWidth(),
        )

        SpacerHorizontal()

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val columnModifier = Modifier
                .weight(1f)

            ThrowHistory(
                data.throws,
                modifier = columnModifier
            )

            SpacerVertical()

            Statistics(
                data.stats,
                modifier = columnModifier
            )
        }

        SpacerHorizontal()
    }
}

data class ScoreScreenData(
    val activeSessionDate: String,
    val prevSessionDate: String,
    val nextSessionDate: String?,
    val throws: List<ThrowHistoryRowData>,
    val stats: List<StatsRowData>,
)