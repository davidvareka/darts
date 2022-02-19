package ladislav.sevcuj.endlessdarts.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ladislav.sevcuj.endlessdarts.TargetProvider
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.db.GameTarget
import ladislav.sevcuj.endlessdarts.db.User
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerHorizontal
import ladislav.sevcuj.endlessdarts.ui.widgets.SpacerVertical

@Composable
fun HomeScreen(
    user: User,
    onNewUser: () -> Unit,
    target: GameTarget,
    onTargetSelect: (GameTarget) -> Unit,
    onStart: () -> Unit,
) {
    var targetDropDownExpanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Ui.padding.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Endless darts",
                style = MaterialTheme.typography.h1
            )

            SpacerHorizontal(32)

            Column(
                modifier = Modifier.width(400.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Player:",
                        fontSize = 20.sp,
                        modifier = Modifier.width(100.dp),
                    )
                    SpacerVertical()
                    Text(
                        user.identifier,
                        fontSize = 20.sp,
                        modifier = Modifier.width(150.dp),
                    )
                    SpacerVertical()
                    Text(
                        "generate new",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                onNewUser()
                            },
                    )
                }
                SpacerHorizontal()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Target:",
                        fontSize = 20.sp,
                        modifier = Modifier.width(100.dp),
                    )
                    SpacerVertical()

                    Box(
                        modifier = Modifier.clickable {
                            targetDropDownExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                                .align(Alignment.CenterEnd),
                            tint = MaterialTheme.colors.onSurface
                        )

                        OutlinedTextField(
                            value = target.label(),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Default,
                                fontSize = 20.sp
                            ),
                            singleLine = true,
                            modifier = Modifier.width(150.dp)
                        )

                        DropdownNavigation(
                            targetDropDownExpanded,
                            onDismiss = {
                                targetDropDownExpanded = false
                            },
                            onTargetSelect = onTargetSelect,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
                SpacerHorizontal()
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("START")
                }
            }
        }
        Spacer(modifier = Modifier.weight(3f))
    }
}

@Composable
private fun DropdownNavigation(
    targetDropDownExpanded: Boolean,
    onDismiss: () -> Unit,
    onTargetSelect: (GameTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = targetDropDownExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        TargetProvider.all.forEach {
            DropdownMenuItem(
                onClick = {
                    onTargetSelect(it)
                    onDismiss()
                }) {
                Text(it.label())
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview(
    widthDp = 600,
)
@Composable
fun HomeScreenPreview() {
    EndlessDartsTheme {
        Surface(color = Color.White) {
            HomeScreen(
                User(
                    id = 1,
                    identifier = "User 1",
                    isTemporary = false,
                ),
                {},
                TargetProvider.getDefault(),
                onTargetSelect = {},
                onStart = {},
            )
        }
    }
}