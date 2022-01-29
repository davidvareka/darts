package ladislav.sevcuj.endlessdarts.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Color(0xFF0d6efd),
    primaryVariant = Color(0xFF0b5ed7),
    secondary = colorSecondary,
    background = Color.White,
    surface = Color.White,
)

@Composable
fun EndlessDartsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}