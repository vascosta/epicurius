package android.epicurius.ui.screens.recipe.profile.components

import android.epicurius.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun EditUserRatingDialog(
    previousRating: Int,
    onEditUserRating: (rating: Int) -> Unit = {},
    onDeleteUserRecipeRating: (rating: Int) -> Unit = {},
    onDismissRequest: () -> Unit = { },
    enableButtons: Boolean
) {
    var newRating by remember { mutableIntStateOf(previousRating) }

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismissRequest },
        confirmButton = {
            Button(
                onClick = {
                    onEditUserRating(newRating)
                    onDismissRequest()
                },
                enabled = enableButtons
            ) { Text("Edit") }
            Button(
                onClick = {
                    onDeleteUserRecipeRating(newRating)
                    onDismissRequest()
                },
                enabled = enableButtons
            ) { Text("Delete Rate") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = enableButtons
            ) { Text("Cancel") }
        },
        title = { Text("Edit Rating") },
        text = {
            Column {
                Text("Select a rating from 1 to 5:")
                Spacer(Modifier.size(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        val isSelected = i <= newRating
                        Image(
                            painter = painterResource(
                                id = if (isSelected) R.drawable.star else R.drawable.white_star
                            ),
                            contentDescription = "Star $i",
                            modifier = Modifier
                                .padding(4.dp)
                                .size(45.dp)
                                .clickable { newRating = i }
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
    )
}