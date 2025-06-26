package android.epicurius.ui.screens.recipe.components

import android.epicurius.R
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.recipe.profile.utils.generateTestImageByteArray
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Base64

@Composable
fun RecipeImage(picture: ByteArray) {
    val bitmap = remember(picture) {
        BitmapFactory.decodeByteArray(picture, 0, picture.size)
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Recipe Image",
            modifier = Modifier
                .height(150.dp)
                .width(325.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
fun RecipeImagePreview() {
    val recipeInfo = RecipeInfo(
        id = 1,
        name = "Recipe Name",
        authorUsername = "ChefBear",
        rating = 4.3,
        cuisine = Cuisine.MEDITERRANEAN,
        mealType = MealType.SIDE_DISH,
        preparationTime = 30,
        servings = 4,
        picture = Base64.getEncoder()
            .encodeToString(generateTestImageByteArray(R.drawable.test_tomato))
    )

    RecipeImage(picture = recipeInfo.pictureBytes)
}
