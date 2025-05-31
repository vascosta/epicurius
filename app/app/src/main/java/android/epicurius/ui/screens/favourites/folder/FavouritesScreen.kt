package android.epicurius.ui.screens.favourites.folder

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FavouritesScreen(
    onBackButton: () -> Unit,
    collections: List<CollectionProfile>
) {
    Scaffold(
        topBar = { TopBar("Favourites", backButton = true, onBackButton) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(10.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                collections.forEach {
                    CollectionProfileBox(it)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun CollectionProfileBox(profile: CollectionProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, Color.Black, RoundedCornerShape(20.dp))
            .height(50.dp)
            .clickable(onClick = {  })
            .padding(start = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(profile.name, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun FavouritesScreenPreview() {
    val collections = listOf(
        CollectionProfile(1, "Italian Delights"),
        CollectionProfile(2, "Quick Snacks"),
        CollectionProfile(3, "Healthy Meals")
    )

    FavouritesScreen({}, collections)
}
