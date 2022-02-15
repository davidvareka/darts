package ladislav.sevcuj.endlessdarts.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.ui.theme.colorSecondary

@Composable
fun NavigationIcon(
    onClick: () -> Unit,
    imageVector: ImageVector,
    text: String,
    isActive: Boolean = false,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            val color = if (isActive) Color.Black else colorSecondary

            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = color,
            )

            SpacerHorizontal(4)

            Text(
                text,
                color = color,
            )
        }
    }
}