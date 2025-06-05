package android.epicurius.ui.screens.collections.favourites.folder.components

import android.epicurius.domain.collection.CollectionProfile
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CollectionProfileBox(
    collection: CollectionProfile,
    onCollectionRequest: (Int) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color.Black, RoundedCornerShape(20.dp))
            .height(50.dp)
            .clickable(onClick = { onCollectionRequest(collection.id) })
            .padding(start = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(collection.name, fontWeight = FontWeight.Bold)
    }
}
