package android.epicurius.ui.screens.user.follow.components

import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.FollowingUser
import android.epicurius.ui.screens.utils.LoadState

data class FollowStateBundle(
    val followersState: LoadState<List<FollowUser>>,
    val followingState: LoadState<List<FollowingUser>>
)
