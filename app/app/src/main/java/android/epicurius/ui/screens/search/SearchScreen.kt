package android.epicurius.ui.screens.search

import android.epicurius.R
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.TabComponent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen() {
    val tabs = listOf("Recipe", "Users")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopBar("Search", backButton = true) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SearchTextField(
                        text = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    TabComponent(tabs, selectedTabIndex, { selectedTabIndex = it })

                    if (selectedTabIndex == 0)
                        Row(modifier = Modifier.fillMaxWidth()) { FiltersIcon() }

                    // search results
                }

                if (selectedTabIndex == 0) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        SearchPhotoComponent()
                    }
                }
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun FiltersIcon() {
    TextButton(
        onClick = {},
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DensityMedium,
            contentDescription = "Filter icon",
            modifier = Modifier
                .size(19.dp)
                .padding(end = 4.dp)
        )
        Text("Filters", fontSize = 15.sp)
    }
}

@Composable
private fun SearchPhotoComponent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {  },
            modifier = Modifier.size(60.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "Camera",
                modifier = Modifier.size(36.dp),
                contentScale = ContentScale.Fit
            )
        }

        Button(
            onClick = { }
        ) {
            Text("Upload")
        }
    }
}

@Preview
@Composable
fun SearchUserScreenPreview() {
    SearchScreen()
}
