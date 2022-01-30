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
    multiplicator: Int,
    onDart: (DartBoard.Field) -> Unit,
    onAction: (DartBoard.Field) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        TargetFields(
            fields = targetFields,
            onDart = onDart,
            multiplicator = multiplicator,
        )
        RegularFields(
            onDart,
            multiplicator = multiplicator,
        )
        ActionButtons(
            onAction,
            multiplicator = multiplicator,
        )
    }
}

@ExperimentalFoundationApi
@Composable
private fun TargetFields(
    fields: List<DartBoard.Field>,
    onDart: (DartBoard.Field) -> Unit,
    multiplicator: Int
) {
    FieldsRow(
        fields = fields,
        onDart = onDart,
        multiplicator = multiplicator,
    )
}

@Composable
private fun FieldsRow(
    fields: List<DartBoard.Field>,
    onDart: (DartBoard.Field) -> Unit,
    multiplicator: Int,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        fields.forEach { field ->
            FieldButton(
                field = field,
                onPress = onDart,
                disabled = multiplicator > field.maxMultiplication,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun RegularFields(
    onDart: (DartBoard.Field) -> Unit,
    multiplicator: Int,
) {
    val fields = mutableListOf<DartBoard.Field>()

    DartBoard.allFields.forEachIndexed { index, field ->
        fields.add(field)

        if ((index + 1) % 7 == 0) {
            FieldsRow(
                fields = fields,
                onDart = onDart,
                multiplicator = multiplicator,
            )
            fields.clear()
        }
    }
}

@Composable
private fun ActionButtons(
    onAction: (DartBoard.Field) -> Unit,
    multiplicator: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        FieldButton(
            field = DartBoard.Field("0", "0", 0),
            onPress = onAction,
            disabled = multiplicator > 1,
            modifier = Modifier.weight(1f),
            backgroundColor = Color(0xff212529),
            borderColor = Color(0xff212529),
        )

        val buttonColor = colorSuccess
        val buttonBackground = Color.White

        FieldButton(
            field = DartBoard.Field("double", "double", null),
            onPress = onAction,
            modifier = Modifier.weight(1f),
            color = if (multiplicator == 2) buttonBackground else buttonColor,
            backgroundColor = if (multiplicator == 2) buttonColor else buttonBackground,
            borderColor = colorSuccess,
        )
        FieldButton(
            field = DartBoard.Field("triple", "triple", null),
            onPress = onAction,
            modifier = Modifier.weight(1f),
            color = if (multiplicator == 3) buttonBackground else buttonColor,
            backgroundColor = if (multiplicator == 3) buttonColor else buttonBackground,
            borderColor = colorSuccess,
        )
        FieldButton(
            field = DartBoard.Field(
                "deleteLast",
                "delete last",
                null,
            ),
            onPress = onAction,
            disabled = multiplicator > 1,
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colors.error,
            borderColor = MaterialTheme.colors.error,
        )
    }
}

@Composable
private fun FieldButton(
    field: DartBoard.Field,
    onPress: (DartBoard.Field) -> Unit,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
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
            enabled = !disabled,
            border = BorderStroke(1.dp, if (disabled) Color.LightGray else borderColor),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = field.label,
                color = if (disabled) Color.Gray else color,
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
        GameKeyboard(listOf(), 1, {}, {},)
    }
}

