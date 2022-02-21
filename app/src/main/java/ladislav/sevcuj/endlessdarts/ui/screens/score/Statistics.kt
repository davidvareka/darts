package ladislav.sevcuj.endlessdarts.ui.screens.score

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.db.ThrowFilter
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRow
import ladislav.sevcuj.endlessdarts.ui.widgets.StatsRowData

@Composable
fun Statistics(
    data: List<StatsRowData>,
    filterIsActive: Boolean,
    onFilter: (ThrowFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Statistics",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                    vertical = Ui.paddingHalved.dp,
                )
            )
            if (filterIsActive) {
                Text(
                    "reset filter",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        onFilter(ThrowFilter.NONE)
                    }
                )
            }
        }

        Divider(color = Color.Black)

        data.forEachIndexed { index, statsData ->
            if (index > 0) {
                Divider()
            }

            StatsRow(
                statsData,
                onFilter
            )
        }

        Divider(color = Color.Black)
    }
}

@Preview
@Composable
private fun StatisticsPreview() {
    EndlessDartsTheme {
        Surface(
            color = Color.White,
        ) {
            Statistics(
                listOf(
                    StatsRowData("Throws", "0"),
                    StatsRowData("Target full success (rate)", "0 (0%)", isSuccess = true, filter = ThrowFilter.FULL_SUCCESS, info = "info"),
                    StatsRowData("Target full miss (rate)", "0 (0%)", isFail = true),
                    StatsRowData("Target hits (rate)", "0 (0%)"),
                    StatsRowData("Throw average", "0"),
                    StatsRowData("Throw max", "0"),
                    StatsRowData("140+", "0", info = "info"),
                    StatsRowData("100+", "0"),
                ), false, onFilter = { }
            )
        }
    }
}