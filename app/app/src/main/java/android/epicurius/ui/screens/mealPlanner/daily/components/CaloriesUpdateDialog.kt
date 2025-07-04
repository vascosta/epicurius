package android.epicurius.ui.screens.mealPlanner.daily.components

import android.epicurius.ui.screens.utils.NumberTextField
import android.epicurius.ui.screens.utils.isValidForNumberTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CaloriesUpdateDialog(
    initialValue: String,
    onUpdateCalories: (calories: Int) -> Unit = {},
    onDismiss: () -> Unit = {},
    enableButtons: Boolean
) {
    var textFieldValue by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    val newCalories = textFieldValue.toIntOrNull()
                    if (newCalories != null) onUpdateCalories(newCalories)
                    onDismiss()
                },
                enabled = enableButtons
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = enableButtons
            ) { Text("Cancel") }
        },
        title = { Text("Update Calories") },
        text = {
            Column {
                Text("Insert new maximum calories for the day:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                NumberTextField(
                    value = textFieldValue,
                    onValueChange = { if (isValidForNumberTextField(it)) textFieldValue = it },
                    enabled = enableButtons,
                    placeholder = "e.g.: 2200",
                )
            }
        }
    )
}

