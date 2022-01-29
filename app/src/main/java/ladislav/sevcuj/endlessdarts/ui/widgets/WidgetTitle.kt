package ladislav.sevcuj.endlessdarts.ui.widgets

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun WidgetTitle(value: String) {
    Text(
        text = value,
        style = MaterialTheme.typography.h3,
    )
}