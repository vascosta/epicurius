package android.epicurius.ui.screens.user.follow

import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.FollowingUser
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.screens.user.components.UserBox
import android.epicurius.ui.screens.user.follow.components.FollowStateBundle
import android.epicurius.ui.screens.user.follow.components.FollowTopBar
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.apiSuccess
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FollowScreen(
    selectedTab: Int,
    followStateBundle: FollowStateBundle,
    followersCount: Int,
    followingCount: Int,
    usersResultState: LoadState<List<SearchUser>>,
    onBackButton: () -> Unit = {},
    onSearchFollowers: (partialFollowersName: String) -> Unit = {},
    onSearchFollowing: (partialFollowingName: String) -> Unit = {},
    onSearchUsersClear: () -> Unit = {},
    onUserProfileRequest: (name: String) -> Unit = {},
    onLoadMoreFollowers: () -> Unit = {},
    onLoadMoreFollowing: () -> Unit = {},
    enableButtons: Boolean
) {
    var showSearchUsersResult by remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(selectedTab) }
    var searchQuery by remember { mutableStateOf("") }
    var searchUsersQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            FollowTopBar(
                selectedTabIndex = selectedTabIndex,
                followersCount = followersCount,
                followingCount = followingCount,
                onTabSelected = { selectedTabIndex = it },
                onBackButton = onBackButton,
                enabled = enableButtons
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchTextField(
                    text = searchQuery,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    onSearchQueryChange = {
                        searchQuery = it
                        showSearchUsersResult = searchQuery.isNotEmpty()
                        if (!showSearchUsersResult) onSearchUsersClear()
                    },
                    onIconClick = {
                        if (showSearchUsersResult) {
                            onSearchUsersClear()
                            if (selectedTab == 0) onSearchFollowers(searchQuery)
                            else onSearchFollowing(searchQuery)
                            searchUsersQuery = searchQuery
                        }
                    },
                    enableButtons = enableButtons
                )
                if (showSearchUsersResult) {
                    LoadStateRenderer(
                        loadState = usersResultState,
                        content = { usersResult ->
                            if (usersResult.isNotEmpty()) {
                                usersResult.forEach { user ->
                                    UserBox(
                                        user = user,
                                        onUserProfileRequest = onUserProfileRequest,
                                        enableButtons = enableButtons
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (selectedTab == 0) onSearchFollowers(searchUsersQuery)
                                        else onSearchFollowing(searchUsersQuery)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    enabled = enableButtons
                                ) { Text("Load More") }
                            }
                            else if (usersResultState is Loaded) {
                                Text(
                                    text = "No users found.",
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        }
                    )
                }
                else {
                    LoadStateRenderer(
                        loadState = if (selectedTabIndex == 0) followStateBundle.followersState
                        else followStateBundle.followingState,
                        content = { users ->
                            if (users.isNotEmpty()) {
                                users.forEach { user ->
                                    UserBox(
                                        user = user,
                                        onUserProfileRequest = onUserProfileRequest,
                                        enableButtons = enableButtons
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (selectedTabIndex == 0) onLoadMoreFollowers()
                                        else onLoadMoreFollowing()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    enabled = enableButtons
                                ) { Text("Load More") }
                            } else if (selectedTabIndex == 0 && followStateBundle.followersState is Loaded) {
                                Text(
                                    text = "No followers found.",
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            } else if (followStateBundle.followingState is Loaded) {
                                Text(
                                    text = "Not following anyone yet.",
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }

                        }
                    )
                }
            }
        },
        containerColor = Color.White
    )
}


@Preview
@Composable
fun FollowPreview() {
    val followers = listOf(
        FollowUser(
            id = 1,
            name = "Jane Smith",
            profilePicture = null
        )
    )

    val following = listOf(
        FollowingUser(
            id = 2,
            name = "Alice Johnson",
            profilePicture = null
        )
    )

    FollowScreen(
        selectedTab = 0,
        followStateBundle = FollowStateBundle(apiSuccess(followers), apiSuccess(following)),
        followersCount = 1,
        followingCount = 1,
        usersResultState = apiSuccess(emptyList()),
        enableButtons = true
    )
}
