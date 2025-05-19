package android.epicurius.ui.screens.user.profile

import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileScreen(username: String, followers: Int, following: Int) {
    Scaffold(
        topBar = { TopBar("Profile") },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                ProfilePicture()

                Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                Text(text = username, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.fillMaxHeight(0.05f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    FollowBox("Followers", followers)
                    FollowBox("Following", following)
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.03f))

                ProfileTabBar()
            }
        }
    )
}

@Composable
private fun ProfilePicture() {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Default Profile",
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
private fun FollowBox(name: String, number: Int) {
    Box(
        modifier = Modifier
            .height(45.dp)
            .width(180.dp)
            .border(width = 1.dp, color = Color.Black, shape = RectangleShape),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = name, fontWeight = FontWeight.Bold)
            Text("$number")
        }
    }
}

@Composable
private fun ProfileTabBar() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("Recipes", "Kitchen Book")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, name ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.padding(15.dp)
                ) {
                    Text(name)
                    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
                }
            }
        }
    }
}

@Preview
@Composable
fun UserProfilePreview() {
    UserProfileScreen("Carol", 1234, 2356)
}