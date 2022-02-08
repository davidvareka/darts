package ladislav.sevcuj.endlessdarts.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.db.Throw
import ladislav.sevcuj.endlessdarts.ui.theme.*
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerVertical
import ladislav.sevcuj.endlessdarts.ui.widgets.WidgetTitle

@Composable
fun GameDashboard(
    currentThrow: Throw,
    lastThrow: Throw?,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        WidgetTitle("Dashboard")

        SpacerHorizontal()

        CurrentThrow(currentThrow)

        SpacerHorizontal()

        LastThrow(lastThrow)
    }
}

@Composable
private fun CurrentThrow(
    data: Throw
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (index in 0..2) {
                if (index > 0) {
                    SpacerVertical()
                }

                val dart = if (index < data.darts.size) {
                    data.darts[index]
                } else {
                    null
                }

                DartScore(
                    order = index + 1,
                    sum = dart?.sum,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SpacerHorizontal()

        CurrentThrowSum(data.throwSummary)
    }
}

@Composable
private fun CurrentThrowSum(
    sum: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Sum",
            modifier = Modifier.weight(1f),
        )

        SpacerVertical()

        DartScore(
            sum = sum,
            order = null,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LastThrow(
    lastThrow: Throw? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (index in 0..2) {
                if (index > 0) {
                    SpacerVertical()
                }

                LastThrowDart(
                    sum = lastThrow?.darts?.get(index)?.sum,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SpacerHorizontal()

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Sum",
                modifier = Modifier.weight(1f),
            )

            SpacerVertical()

            LastThrowDart(
                sum = lastThrow?.throwSummary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DartScore(
    order: Int?,
    sum: Int?,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        order?.let {
            Text(
                text = "Dart $it",
            )

            SpacerHorizontal()
        }

        Surface(
            color = colorDangerBackground,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .border(BorderStroke(1.dp, colorDangerBorder))
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    sum?.toString() ?: "-",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = colorDangerText,
                    modifier = Modifier
                        .padding(vertical = Ui.paddingHalved.dp)
                )
            }
        }
    }
}

@Composable
private fun LastThrowDart(
    sum: Int?,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = colorAlertBackground,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .border(BorderStroke(1.dp, colorAlertBorder))
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                sum?.toString() ?: "-",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = colorAlertText,
                modifier = Modifier
                    .padding(vertical = Ui.paddingHalved.dp)
            )
        }
    }
}

@Preview
@Composable
private fun GameDashboardPreview() {
    EndlessDartsTheme {
        GameDashboard(
            Throw(
                id = 1,
                sessionId = 1,
                player = "Preview",
                target = "20",
            ),
            null
        )
    }
}