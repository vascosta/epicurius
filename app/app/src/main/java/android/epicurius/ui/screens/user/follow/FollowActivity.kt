package android.epicurius.ui.screens.user.follow

import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.user.follow.components.FollowStateBundle
import android.epicurius.ui.screens.user.profile.UserProfileActivity
import android.epicurius.ui.screens.utils.Idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FollowActivity : EpicuriusActivity() {
    override val viewModel: FollowViewModel by getViewModel<FollowViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            combine(
                viewModel.followers,
                viewModel.following
            ) { followersState, followingState -> followersState to followingState }
                .collectLatest { (followersState, followingState) ->
                    val username = intent.getStringExtra(Intents.USERNAME) ?: ""
                    if (followersState is Idle) viewModel.getFollowers(username, null)
                    if (followingState is Idle) viewModel.getFollowing(username, null)
                }
        }
        super.onCreate(savedInstanceState)
        setContent {
            val followersState = viewModel.followers.collectAsState(Idle)
            val followingState = viewModel.following.collectAsState(Idle)
            val searchUsersState = viewModel.searchedUsers.collectAsState(Idle)
            MaterialTheme {
                FollowScreen(
                    selectedTab = intent.getIntExtra(Intents.FOLLOW_TAB, 0),
                    followStateBundle = FollowStateBundle(followersState.value, followingState.value),
                    followersCount = intent.getIntExtra(Intents.FOLLOWERS_COUNT, -1),
                    followingCount = intent.getIntExtra(Intents.FOLLOWING_COUNT, -1),
                    usersResultState = searchUsersState.value,
                    onBackButton = { navigateTo<UserProfileActivity>(useStack = true, finishCurrent = true) },
                    onSearchFollowers = { partialFollowers ->
                        viewModel.getFollowers(
                            intent.getStringExtra(Intents.USERNAME) ?: "",
                            partialFollowers
                        )
                    },
                    onSearchFollowing = { partialFollowing ->
                        viewModel.getFollowing(
                            intent.getStringExtra(Intents.USERNAME) ?: "",
                            partialFollowing
                        )
                    },
                    onSearchUsersClear = { viewModel.clearSearchUsers() },
                    onUserProfileRequest = ::navigateToUserProfileActivity,
                    onLoadMoreFollowers = {
                        viewModel.getFollowers(intent.getStringExtra(Intents.USERNAME) ?: "", null)
                    },
                    onLoadMoreFollowing = {
                        viewModel.getFollowing(intent.getStringExtra(Intents.USERNAME) ?: "", null)
                    },
                    enableButtons = viewModel.enableButtons
                )
            }
        }
    }

    private fun navigateToUserProfileActivity(name: String) {
        navigateTo<UserProfileActivity> { intent ->
            intent.putExtra(Intents.USERNAME, name)
        }
    }
}