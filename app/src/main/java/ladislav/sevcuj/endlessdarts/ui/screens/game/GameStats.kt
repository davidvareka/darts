package ladislav.sevcuj.endlessdarts.ui.screens.game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRow
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData
import ladislav.sevcuj.endlessdarts.ui.widgets.WidgetTitle

@Composable
fun GameStats(
    data: List<StatsRowData>,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        WidgetTitle(value = "Stats")

        SpacerHorizontal()

        data.forEach {
            StatsRow(it, onFilter = null)
            Divider()
        }
    }
}

@Preview
@Composable
private fun GameStatsPreview() {
    EndlessDartsTheme {
        GameStats(
            listOf(
                StatsRowData("Throws", "0"),
                StatsRowData(
                    "Target full success (rate)",
                    "0 (0%)",
                    isSuccess = true,
                    info = "info"
                ),
                StatsRowData("Target full miss (rate)", "0 (0%)", isFail = true),
                StatsRowData("Target hits (rate)", "0 (0%)"),
                StatsRowData("Throw average", "0"),
                StatsRowData("Throw max", "0"),
                StatsRowData("140+", "0", info = "info"),
                StatsRowData("100+", "0"),
            )
        )
    }
}
