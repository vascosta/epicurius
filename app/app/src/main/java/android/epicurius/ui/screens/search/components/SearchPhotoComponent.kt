package android.epicurius.ui.screens.search.components

import android.epicurius.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SearchPhotoComponent(
    onCamera: () -> Unit,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier,
    enableButtons: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onCamera,
            modifier = Modifier.size(60.dp),
            enabled = enableButtons
        ) {
            Image(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "Camera",
                modifier = Modifier.size(36.dp),
                contentScale = ContentScale.Fit
            )
        }

        Button(
            onClick = onUpload,
            enabled = enableButtons,
        ) { Text("Upload") }
    }
}