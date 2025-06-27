package android.epicurius.ui.screens.recipe.preparation

import android.epicurius.domain.recipe.Instructions
import android.epicurius.ui.screens.recipe.preparation.components.RateRecipeDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun PreparationContent(
    instructions: Instructions,
    onRateRecipe: (Int) -> Unit,
    onSkipRating: () -> Unit,
    onCancelPreparation: () -> Unit,
    paddingValues: PaddingValues
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var showRateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Preparation:",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        instructions.steps.entries.forEach { (stepNumber, instruction) ->
            val backgroundColor =
                if (stepNumber.toInt() == currentStep) Color(0xFFCDFA7D)
                else Color.Transparent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(8.dp)
            ) {
                Text("$stepNumber. $instruction")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { if (currentStep < instructions.steps.size) currentStep++ }
            ) {
                if (currentStep >= instructions.steps.size)
                    Text(
                        text = "Done",
                        modifier = Modifier.clickable { showRateDialog = true }
                    )
                else Text("Next")
            }

            Button(onClick = { onCancelPreparation() }) { Text("Cancel") }
        }

        if (showRateDialog) {
            RateRecipeDialog(
                onDismissRequest = { showRateDialog = false },
                onSkipRating = { onSkipRating() },
                onRateRecipe = { rating -> onRateRecipe(rating) }
            )
        }
    }
}