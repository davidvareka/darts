package ladislav.sevcuj.endlessdarts.ui.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpacerHorizontal(
    size: Int = 8,
) {
    Spacer(modifier = Modifier.height(size.dp))
}