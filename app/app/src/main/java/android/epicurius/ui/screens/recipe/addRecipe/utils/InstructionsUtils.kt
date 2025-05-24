package android.epicurius.ui.screens.recipe.addRecipe.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InstructionsComponent(
    steps: List<String>,
    onStepsChange: (List<String>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Instructions", style = MaterialTheme.typography.titleMedium)

        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = step,
                    onValueChange = { newStep ->
                        val updatedSteps = steps.toMutableList()
                        updatedSteps[index] = newStep
                        onStepsChange(updatedSteps)
                    },
                    label = { Text("Step ${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                DeleteFieldButton(
                    onClick = {
                        val updatedSteps = steps.toMutableList()
                        updatedSteps.removeAt(index)
                        onStepsChange(updatedSteps)
                    }
                )
            }
        }

        val canAddField = steps.isEmpty() || steps.last().isNotBlank()

        AddFieldButton(
            onClick = {
                if (!canAddField) return@AddFieldButton
                onStepsChange(steps + "")
            },
            modifier = Modifier.padding(top = 8.dp),
            text = "Add Step"
        )
    }
}
