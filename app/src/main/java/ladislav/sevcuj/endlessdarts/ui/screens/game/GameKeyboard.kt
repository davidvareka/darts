package ladislav.sevcuj.endlessdarts.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ladislav.sevcuj.endlessdarts.DartBoard
import ladislav.sevcuj.endlessdarts.Ui
import ladislav.sevcuj.endlessdarts.ui.theme.EndlessDartsTheme
import ladislav.sevcuj.endlessdarts.ui.theme.colorSuccess

@ExperimentalFoundationApi
@Composable
fun GameKeyboard(
    targetFields: List<DartBoard.Field>,
    onDart: (DartBoard.Field) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        TargetFields(fields = targetFields, onDart = onDart)
        RegularFields(onDart)
        ActionButtons()
    }
}

@ExperimentalFoundationApi
@Composable
private fun TargetFields(
    fields: List<DartBoard.Field>,
    onDart: (DartBoard.Field) -> Unit,
) {
    FieldsRow(fields = fields, onDart = onDart)
}

@Composable
private fun FieldsRow(
    fields: List<DartBoard.Field>,
    onDart: (DartBoard.Field) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        fields.forEach { field ->
            FieldButton(
                field = field,
                onPress = onDart,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun RegularFields(
    onDart: (DartBoard.Field) -> Unit,
) {
    val fields = mutableListOf<DartBoard.Field>()

    DartBoard.allFields.forEachIndexed { index, field ->
        fields.add(field)

        if ((index + 1) % 7 == 0) {
            FieldsRow(
                fields = fields,
                onDart = onDart,
            )
            fields.clear()
        }
    }
}

@Composable
private fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        FieldButton(
            field = DartBoard.Field("0", "0"),
            onPress = {},
            backgroundColor = Color(0xff212529),
            borderColor = Color(0xff212529),
            modifier = Modifier.weight(1f),
        )
        FieldButton(
            field = DartBoard.Field("double", "double", null),
            onPress = {},
            modifier = Modifier.weight(1f),
            color = colorSuccess,
            borderColor = colorSuccess,
            backgroundColor = Color.White,
        )
        FieldButton(
            field = DartBoard.Field("triple", "triple", null),
            onPress = {},
            modifier = Modifier.weight(1f),
            color = colorSuccess,
            borderColor = colorSuccess,
            backgroundColor = Color.White,
        )
        FieldButton(
            field = DartBoard.Field(
                "deleteLast",
                "delete last",
                null,
            ),
            onPress = {},
            backgroundColor = MaterialTheme.colors.error,
            borderColor = MaterialTheme.colors.error,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FieldButton(
    field: DartBoard.Field,
    onPress: (DartBoard.Field) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    backgroundColor: Color = MaterialTheme.colors.primary,
    borderColor: Color = MaterialTheme.colors.primary,
) {
    Box(
        modifier = modifier
            .padding((Ui.paddingHalved / 2).dp)
    ) {
        Button(
            onClick = { onPress(field) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColor,
            ),
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = field.label,
                color = color,
                fontSize = 12.sp,
            )
        }
    }
}

@ExperimentalFoundationApi
@Preview(
    widthDp = 600,
)
@Composable
private fun GameKeyboardPreview() {
    EndlessDartsTheme {
        GameKeyboard(listOf(), {})
    }
}

