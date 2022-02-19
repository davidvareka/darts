package ladislav.sevcuj.endlessdarts.ui.screens.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ladislav.sevcuj.endlessdarts.TargetProvider
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.db.GameTarget
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme

@Composable
fun TargetPickerDialog(
    onPick: (GameTarget) -> Unit,
) {
    Surface(color = Color.Black.copy(alpha = 0.5f))
    {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TargetProvider.all.forEachIndexed { index, target ->
                        if (index > 0) {
                            Divider()
                        }
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onPick(target)
                                }
                                .padding(Ui.paddingHalved.dp)
                        ) {
                            Text(
                                target.label(),
                                modifier = Modifier
                                    .fillMaxWidth()

                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    widthDp = 600,
    heightDp = 300
)
@Composable
fun TargetPickerDialogPreview() {
    EndlessDartsTheme {
        Surface(
            color = Color.White,
        ) {
            TargetPickerDialog(onPick = {})
        }
    }
}