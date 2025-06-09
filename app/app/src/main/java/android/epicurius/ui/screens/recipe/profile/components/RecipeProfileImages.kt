package android.epicurius.ui.screens.recipe.profile.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun RecipeProfileImages(
    images: List<ByteArray>,
    pagerState: PagerState,
    onImageClick: () -> Unit,
    enabled: Boolean
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(250.dp)
    ) { page ->
        val bitmap = remember(images[page]) {
            BitmapFactory.decodeByteArray(images[page], 0, images[page].size)
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Recipe Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    enabled = enabled,
                    onClick = { onImageClick() }
                )
        )
    }
}

