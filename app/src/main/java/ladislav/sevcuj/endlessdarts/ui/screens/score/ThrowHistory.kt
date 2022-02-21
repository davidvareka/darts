package ladislav.sevcuj.endlessdarts.ui.screens.score

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.theme.colorFail
import ladislav.sevcuj.endlessdarts.ui.theme.colorSuccess
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal

@Composable
fun ThrowHistory(
    data: List<ThrowHistoryRowData>,
    filterIsActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        ThrowHistoryHead(
            filterIsActive = filterIsActive,
        )

        Divider(color = Color.Black)

        if (data.isEmpty()) {
            Text(
                "No data available",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        } else {
            ThrowHistoryBody(
                data,
                filterIsActive = filterIsActive,
            )
            Divider(color = Color.Black)
            SpacerHorizontal(Ui.paddingHalved)
            Text(
                text = "Last throw on top",
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun ThrowHistoryHead(filterIsActive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val modifier = Modifier.weight(1f)

        if (filterIsActive) {
            ThrowHistoryHeadCell(
                title = "#",
                modifier = modifier,
            )
        }

        ThrowHistoryHeadCell(
            title = "target",
            modifier = modifier,
        )

        ThrowHistoryHeadCell(
            title = "dart 1",
            modifier = modifier,
        )

        ThrowHistoryHeadCell(
            title = "dart 2",
            modifier = modifier,
        )

        ThrowHistoryHeadCell(
            title = "dart 3",
            modifier = modifier,
        )

        ThrowHistoryHeadCell(
            title = "sum",
            modifier = modifier,
        )

        ThrowHistoryHeadCell(
            title = "average",
            modifier = modifier,
        )
    }
}

@Composable
private fun ThrowHistoryHeadCell(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(
                vertical = Ui.paddingHalved.dp,
            ),
    )
}

@Composable
private fun ThrowHistoryBody(data: List<ThrowHistoryRowData>, filterIsActive: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        data.forEachIndexed { index, item ->
            if (index > 0) {
                Divider()
            }

            ThrowHistoryRow(data = item, filterIsActive)
        }
    }
}

@Composable
private fun ThrowHistoryRow(
    data: ThrowHistoryRowData,
    filterIsActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val modifier = Modifier.weight(1f)

        if (filterIsActive) {
            ThrowHistoryValueCell(
                data.order.toString(),
                modifier,
            )
        }

        ThrowHistoryValueCell(
            data.target,
            modifier,
            color = if (
                !data.dart1.isSuccess
                && !data.dart2.isSuccess
                && !data.dart3.isSuccess
            ) {
                colorFail
            } else {
                Color.Transparent
            },
        )

        ThrowHistoryValueCell(
            data.dart1.value,
            modifier,
            color = if (
                data.dart1.isSuccess
            ) {
                colorSuccess
            } else {
                Color.Transparent
            },
        )

        ThrowHistoryValueCell(
            data.dart2.value,
            modifier,
            color = if (
                data.dart2.isSuccess
            ) {
                colorSuccess
            } else {
                Color.Transparent
            },
        )

        ThrowHistoryValueCell(
            data.dart3.value,
            modifier,
            color = if (
                data.dart3.isSuccess
            ) {
                colorSuccess
            } else {
                Color.Transparent
            },
        )

        ThrowHistoryValueCell(
            data.sum.toString(),
            modifier,
        )

        ThrowHistoryValueCell(
            data.average.toString(),
            modifier,
        )
    }
}

@Composable
private fun ThrowHistoryValueCell(
    value: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
) {
    Surface(
        color = color,
        modifier = modifier,
    ) {
        Text(
            text = value,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = Ui.paddingHalved.dp,
                ),
        )
    }
}

data class ThrowHistoryRowData(
    val throwId: Long,
    val order: Int,
    val target: String,
    val dart1: Dart,
    val dart2: Dart,
    val dart3: Dart,
    val sum: Int,
    val average: Int,
) {
    data class Dart(
        val value: String,
        val isSuccess: Boolean,
    )
}

@Preview(
    heightDp = 300,
)
@Composable
fun ThrowHistoryPreview() {
    EndlessDartsTheme {
        Surface(
            color = Color.White,
        ) {
            ThrowHistory(listOf(
                ThrowHistoryRowData(
                    1,
                    1,
                    "20",
                    ThrowHistoryRowData.Dart("20", true),
                    ThrowHistoryRowData.Dart("15", false),
                    ThrowHistoryRowData.Dart("T20", true),
                    95,
                    31,
                ),
                ThrowHistoryRowData(
                    2,
                    2,
                    "20",
                    ThrowHistoryRowData.Dart("1",  false),
                    ThrowHistoryRowData.Dart("1", false),
                    ThrowHistoryRowData.Dart("1", false),
                    3,
                    1,
                ),
                ThrowHistoryRowData(
                    3,
                    3,
                    "20",
                    ThrowHistoryRowData.Dart("5", false),
                    ThrowHistoryRowData.Dart("5", false),
                    ThrowHistoryRowData.Dart("5", false),
                    15,
                    5,
                ),
            ), false)
        }
    }
}