package android.epicurius.ui.screens.collections.components

import android.epicurius.domain.collection.CollectionProfile
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CollectionProfileBox(
    collection: CollectionProfile,
    onCollectionDelete: (collectionId: Int) -> Unit = {},
    onCollectionRequest: (collectionId: Int) -> Unit = {},
    enableButtons: Boolean
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color.Black, RoundedCornerShape(20.dp))
            .height(60.dp)
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = enableButtons,
                    onClick = { onCollectionRequest(collection.id) }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = collection.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp)
            )
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Collection",
                    tint = Color.Red
                )
            }
        }
        if (showDeleteDialog) {
            DeleteCollectionDialog(
                collectionId = collection.id,
                collectionName = collection.name,
                onCollectionDelete = onCollectionDelete,
                onDismissRequest = { if (enableButtons) showDeleteDialog = false },
                enableButtons = enableButtons
            )
        }
    }
}

@Preview
@Composable
fun CollectionProfileBoxPreview() {
    CollectionProfileBox(CollectionProfile(1, "Italian Delights"), {}, {}, true)
}
