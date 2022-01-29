package ladislav.sevcuj.endlessdarts.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.db.Target
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.theme.colorWarningBackground
import ladislav.sevcuj.endlessdarts.ui.theme.colorWarningBorder
import ladislav.sevcuj.endlessdarts.ui.theme.colorWarningText
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal
import ladislav.sevcuj.endlessdarts.ui.widgets.WidgetTitle

@Composable
fun GameTarget(
    target: Target,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        WidgetTitle("Target")

        SpacerHorizontal()

        Target(identifier = target.label)

        SpacerHorizontal()

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary,
            )

        ) {
            Text(
                text = "Change Target",
                color = Color.White,
            )
        }
    }
}

@Composable
private fun Target(identifier: String) {
    Surface(
        color = colorWarningBackground,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(BorderStroke(1.dp, colorWarningBorder))
            .fillMaxWidth()
    ) {
        Text(
            identifier,
            fontSize = 64.sp,
            textAlign = TextAlign.Center,
            color = colorWarningText,
            modifier = Modifier
                .padding(vertical = Ui.padding.dp)
        )
    }
}

@Preview(
    widthDp = 200
)
@Composable
private fun GameTargetPreview() {
    EndlessDartsTheme {
        GameTarget(Target(1, "20"))
    }
}