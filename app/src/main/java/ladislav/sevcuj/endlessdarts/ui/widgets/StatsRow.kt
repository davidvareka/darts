package ladislav.sevcuj.endlessdarts.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme

@Composable
fun StatsRow(
    data: StatsRowData,
) {
    val color = when {
        data.isSuccess -> {
            Color(0xFF198754)
        }
        data.isFail -> {
            Color(0xFFdc3545)
        }
        else -> {
            Color.Transparent
        }
    }

    val verticalPadding = Ui.paddingSmallest.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = verticalPadding),
        ) {
            Text(
                text = data.label,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
            )

            data.info?.let {
                SpacerVertical(4)

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                        //TODO
                    }
                )
            }

            if (data.showDetail) {
                SpacerVertical(4)

                Text(
                    text = "(show)",
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        //TODO
                    }
                )
            }
        }

        SpacerVertical()

        Text(
            text = data.value,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.End,
            modifier = Modifier
                .background(color)
                .width(125.dp)
                .padding(vertical = verticalPadding),
        )
    }
}

data class StatsRowData(
    val label: String,
    val value: String,
    val isSuccess: Boolean = false,
    val isFail: Boolean = false,
    val showDetail: Boolean = false,
    val info: String? = null,
)

@Preview
@Composable
private fun StatsRowPreview() {
    EndlessDartsTheme {
        Surface(
            color = Color.White,
        ) {
            StatsRow(
                data = StatsRowData(
                    "Target full success (rate)",
                    "0 (0%)",
                    isSuccess = true,
                    showDetail = true,
                    info = "Info",
                )
            )
        }
    }
}
