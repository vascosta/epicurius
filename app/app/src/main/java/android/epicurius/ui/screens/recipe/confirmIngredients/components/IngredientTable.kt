package android.epicurius.ui.screens.recipe.confirmIngredients.components

import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IngredientTable(
    ingredients: List<Ingredient>,
    checkboxStates: List<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit,
    onNameClick: (Ingredient) -> Unit
) {
    ingredients.forEachIndexed { index, ingredient ->
        IngredientBulletPoint(
            ingredient = ingredient,
            checked = checkboxStates[index],
            onCheckedChange = { isChecked -> onCheckedChange(index, isChecked) },
            onNameClick = { onNameClick(ingredient) }
        )
    }
}


@Composable
private fun IngredientBulletPoint(
    ingredient: Ingredient,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onNameClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient.name,
            modifier = Modifier
                .weight(0.4f)
                .clickable { onNameClick() },
            fontSize = 18.sp
        )

        Text(
            text = if (ingredient.quantity % 1.0 == 0.0)
                ingredient.quantity.toInt().toString()
            else
                ingredient.quantity.toString(),
            modifier = Modifier.weight(0.2f),
            fontSize = 18.sp
        )

        Text(
            text = if (ingredient.unit == IngredientUnit.X) "" else ingredient.unit.displayName,
            modifier = Modifier.weight(0.2f),
            fontSize = 18.sp
        )

        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier.weight(0.1f)
        )

        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Shopping List",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(0.1f)
        )
    }
}