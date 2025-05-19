package android.epicurius.ui.screens

import android.epicurius.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(topBarText: String, backButton: Boolean = false) {
    TopAppBar(
        title = { Text(topBarText) },
        modifier = Modifier
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        navigationIcon = {
            if (backButton) {
                IconButton(
                    onClick = {  },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                        contentDescription = stringResource(R.string.app_name)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        ),
    )
}

@Composable
fun BottomBar() {
    NavigationBar(containerColor = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth()
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            color = Color.Black,
                            start = Offset(0f, 1f),
                            end = Offset(size.width, 1f),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomBarButton({}, R.drawable.home, "Home")
            BottomBarButton({}, R.drawable.pencil, "Pencil")
            BottomBarButton({}, R.drawable.magnifier, "Magnifier", 41)
            BottomBarButton({}, R.drawable.plate, "Plate", 45)
            BottomBarButton({}, R.drawable.star, "Star")
        }
    }
}

@Composable
private fun BottomBarButton(onClick: () -> Unit, imageId: Int, description: String, imageSize: Int = 36) {
    IconButton(onClick = onClick, modifier = Modifier.size(70.dp)) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = description,
            modifier = Modifier.size(imageSize.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview
@Composable
fun NavBarPreview() {
    TopBar("Settings", true)
}

@Preview
@Composable
fun BottomBarPreview() {
    BottomBar()
}