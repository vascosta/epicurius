package android.epicurius.ui.screens.recipe.preparation

import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.recipe.preparation.components.RateRecipeDialog
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
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

@Composable
fun PreparationContent(
    recipeState: LoadState<Recipe>,
    userRecipeRatingState: LoadState<Int?>,
    onRateRecipe: (rating: Int) -> Unit,
    onDeleteUserRecipeRating: (rating: Int) -> Unit = {},
    onSkipRating: () -> Unit,
    onCancelPreparation: () -> Unit,
    enableButtons: Boolean,
    paddingValues: PaddingValues
) {
    var showRateDialog by remember { mutableStateOf(false) }

    var currentStep by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadStateRenderer(
            loadState = recipeState,
            content = { recipe ->
                Text(
                    text = "Preparation:",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                recipe.instructions.steps.entries.forEach { (stepNumber, instruction) ->
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
                        onClick = { if (currentStep < recipe.instructions.steps.size) currentStep++ },
                        enabled = enableButtons
                    ) {
                        if (currentStep >= recipe.instructions.steps.size)
                            Text(
                                text = "Done",
                                modifier = Modifier.clickable { showRateDialog = true }
                            )
                        else Text("Next")
                    }
                    Button(
                        onClick = { onCancelPreparation() },
                        enabled = enableButtons
                    ) { Text("Cancel") }
                }
                if (showRateDialog) {
                    LoadStateRenderer(
                        loadState = userRecipeRatingState,
                        content = { userRecipeRating ->
                            RateRecipeDialog(
                                previousRating = userRecipeRating ?: 0,
                                onRateRecipe = onRateRecipe,
                                onDeleteUserRecipeRating = onDeleteUserRecipeRating,
                                onDismissRequest = {
                                    showRateDialog = false
                                    onSkipRating()
                                },
                                enableButtons = enableButtons
                            )
                        }
                    )

                }
            }
        )
    }
}