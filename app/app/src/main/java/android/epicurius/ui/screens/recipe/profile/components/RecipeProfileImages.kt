package android.epicurius.ui.screens.recipe.profile.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun RecipeProfileImages(
    images: List<ByteArray>,
    pagerState: PagerState,
    isAuthor: Boolean,
    onImageClick: () -> Unit,
    onAddImage: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    enableButtons: Boolean
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(250.dp)
    ) { page ->
        if (page < images.size) {
            val bitmap = remember(images[page]) {
                BitmapFactory.decodeByteArray(images[page], 0, images[page].size)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            enabled = enableButtons,
                            onClick = onImageClick
                        )
                )

                if (enableButtons && images.size > 1) {
                    IconButton(
                        onClick = { onRemoveImage(page) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Image",
                            tint = Color.White
                        )
                    }
                }
            }
        } else if (isAuthor) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .clickable(enabled = enableButtons, onClick = onAddImage),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}

