package android.epicurius.ui.screens.recipe

import android.epicurius.R
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.ui.screens.utils.MixedText
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp

@Composable
fun RecipeInfoBox(recipeInfo: RecipeInfo) {
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

            RecipeDetails(
                preparationTime = recipeInfo.preparationTime,
                servings = recipeInfo.servings,
                rating = 4.3f // Placeholder rating, replace with actual data
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

@Composable
fun RecipeHeader(name: String, author: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MixedText(boldString = "by ", normalString = author)

            IconButton(
                onClick = { },
                modifier = Modifier.size(24.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.white_star),
                    contentDescription = "Favorites",
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
    RecipeInfoBox(
        recipeInfo = RecipeInfo(
            id = 1,
            name = "Recipe Name",
            authorUsername = "ChefBear",
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
            )
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
            cuisine = Cuisine.ASIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 20,
            servings = 2,
            picture = "".toByteArray()
        )
    )
}
