package android.epicurius.ui.screens.recipe

import android.epicurius.R
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeInfo(recipeInfo: RecipeInfo) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .background(Color.White)
            .border(0.05.dp, Color.Black)
            .padding(5.dp)
    ) {
        RecipeHeader(name = recipeInfo.name)

        RecipeImage(recipeInfo.picture)

        RecipeDetails(
            preparationTime = recipeInfo.preparationTime,
            servings = recipeInfo.servings,
            rating = 4.3f // exemplo de rating, alterar para get de rating
        )
    }
}

@Composable
fun RecipeHeader(name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, fontWeight = FontWeight.Bold)

        IconButton(onClick = {  }) {
            Image(
                painter = painterResource(R.drawable.white_star),
                contentDescription = "Favorites",
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

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
                .fillMaxWidth()
                .height(100.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun RecipeDetails(preparationTime: Int, servings: Int, rating: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        InfoItem(icon = Icons.Filled.PunchClock, text = "$preparationTime min")
        InfoItem(icon = Icons.Filled.People, text = "$servings px")
        InfoItem(icon = Icons.Filled.Star, text = "$rating/5")
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(text, fontSize = 10.sp)
    }
}

@Preview
@Composable
fun RecipeInfoPreview() {
    RecipeInfo(
        recipeInfo = RecipeInfo(
            id = 1,
            name = "Recipe Name",
            cuisine = Cuisine.MEDITERRANEAN,
            mealType = MealType.SIDE_DISH,
            preparationTime = 30,
            servings = 4,
            picture = "".toByteArray()
        )
    )
}