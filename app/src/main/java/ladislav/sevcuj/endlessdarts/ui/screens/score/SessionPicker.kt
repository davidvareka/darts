package ladislav.sevcuj.endlessdarts.ui.screens.score

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.DateInstance
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.asDateString
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerVertical

@Composable
fun SessionPicker(
    active: String,
    prev: String,
    next: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = modifier
        ) {
            val itemModifier = Modifier.width(75.dp)

                ClickableSessionLabel(
                    date = prev,
                    onClick = onSelect,
                    modifier = itemModifier,
                )
                SpacerVertical(Ui.padding * 2)

            ActiveSessionLabel(
                date = active,
                modifier = itemModifier,
            )

            SpacerVertical(Ui.padding * 2)
            ClickableSessionLabel(
                date = next,
                onClick = onSelect,
                modifier = itemModifier,
            )
        }
    }
}

@Composable
private fun ActiveSessionLabel(
    date: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = date,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun ClickableSessionLabel(
    date: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = date,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline,
        color = Color.Blue,
        modifier = modifier.clickable { onClick(date) }
    )
}

@Preview
@Composable
fun SessionPickerPreview() {
    EndlessDartsTheme {
        SessionPicker(
            active = DateInstance.now().asDateString(),
            prev = DateInstance.now().asDateString(),
            next = DateInstance.now().asDateString(),
            {}
        )
    }
}
