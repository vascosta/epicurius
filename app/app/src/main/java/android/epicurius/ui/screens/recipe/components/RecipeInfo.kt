package android.epicurius.ui.screens.recipe.components

import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RecipeInfoBox(
    recipeInfo: RecipeInfo,
    onRecipeRequest: (Int) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color.Black, RoundedCornerShape(20.dp))
            .padding(5.dp)
            .clickable(onClick = { onRecipeRequest(recipeInfo.id) })
    ) {
        Column {
            RecipeHeader(name = recipeInfo.name, author = recipeInfo.authorUsername)

            RecipeImage(recipeInfo.picture)

            RecipeDetails(
                preparationTime = recipeInfo.preparationTime,
                servings = recipeInfo.servings,
                rating = recipeInfo.rating
            )
        }
    }
}

@Composable
fun RecipeInfoSimpleBox(recipeInfo: RecipeInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color.Black, RoundedCornerShape(20.dp))
            .padding(5.dp)
            .clickable(onClick = {  })
    ) {
        Column {
            RecipeHeader(name = recipeInfo.name, author = recipeInfo.authorUsername)
            RecipeImage(recipeInfo.picture)
        }
    }
}

@Preview
@Composable
fun RecipeInfoPreview() {
    RecipeInfoBox(
        recipeInfo = RecipeInfo(
            id = 1,
            name = "Recipe Name",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.SIDE_DISH,
            preparationTime = 30,
            servings = 4,
            picture = byteArrayOf(
                0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(), 0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x0D.toByte(), 0x49.toByte(), 0x48.toByte(), 0x44.toByte(), 0x52.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                0x08.toByte(), 0x02.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x90.toByte(), 0x77.toByte(), 0x53.toByte(),
                0xDE.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x0A.toByte(), 0x49.toByte(), 0x44.toByte(), 0x41.toByte(),
                0x54.toByte(), 0x08.toByte(), 0xD7.toByte(), 0x63.toByte(), 0xF8.toByte(), 0xCF.toByte(), 0xC0.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x04.toByte(), 0x00.toByte(), 0x01.toByte(), 0xE2.toByte(), 0x26.toByte(), 0x05.toByte(), 0x9B.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x49.toByte(), 0x45.toByte(), 0x4E.toByte(), 0x44.toByte(),
                0xAE.toByte(), 0x42.toByte(), 0x60.toByte(), 0x82.toByte()
            ),
        )
    )
}

@Preview
@Composable
fun RecipeInfoSimpleBoxPreview() {
    RecipeInfoSimpleBox(
        recipeInfo = RecipeInfo(
            id = 1,
            name = "Simple Recipe Name",
            authorUsername = "ChefBear",
            rating = 4.3,
            cuisine = Cuisine.ASIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 20,
            servings = 2,
            picture = "".toByteArray()
        )
    )
}
