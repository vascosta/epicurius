package android.epicurius.ui.screens.recipe.components

import android.epicurius.R
import android.epicurius.ui.screens.utils.MixedText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeHeader(
    name: String,
    author: String,
    isFavourite: Boolean = false,
    onFavouritesClick: () -> Unit = {  }
) {
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
                onClick = { onFavouritesClick() },
                modifier = Modifier.size(24.dp),
            ) {
                val painter = if (isFavourite) {
                    painterResource(R.drawable.star)
                } else {
                    painterResource(R.drawable.white_star)
                }

                Image(
                    painter = painter,
                    contentDescription = "Favorites",
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}