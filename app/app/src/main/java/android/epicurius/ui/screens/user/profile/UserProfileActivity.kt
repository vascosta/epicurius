package android.epicurius.ui.screens.user.profile

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.RecipeInfo
import android.epicurius.domain.user.FollowUser
import android.epicurius.domain.user.FollowingUser
import android.epicurius.domain.user.UserProfile
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.screens.user.follow.FollowActivity
import android.epicurius.ui.screens.user.settings.SettingsActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.apiSuccess
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UserProfileActivity : EpicuriusActivity() {
    override val viewModel: UserProfileViewModel by getViewModel<UserProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            combine(
                viewModel.userProfile,
                viewModel.userRecipes,
                viewModel.userKitchenBook,
                viewModel.userFollowers,
                viewModel.userFollowing
            ) { userProfile, userRecipes, userKitchenBook, userFollowers, userFollowing ->
                UserProfileStateBundle(userProfile, userRecipes, userKitchenBook, userFollowers, userFollowing)
            }.collectLatest { state ->
                val userProfileName = intent.getStringExtra(Intents.USERNAME) ?: viewModel.session.getUserName()

                if (state.followers is Idle && state.following is Idle) {
                    viewModel.getUserFollowers()
                    viewModel.getUserFollowing()
                }
                if (state.followers is Loaded &&
                    state.following is Loaded &&
                    state.profile is Idle
                    ) {
                    viewModel.getUserProfile(userProfileName)
                }
                if (state.profile is Loaded && state.recipes is Idle) viewModel.getUserRecipes()
                if (state.profile is Loaded && state.kitchenBook is Idle) viewModel.getUserKitchenBook()

            }
        }
        setContent {
            val userProfile = viewModel.userProfile.collectAsState()
            MaterialTheme {
                UserProfileScreen(
                    isAnotherUserProfile = viewModel.isAnotherUserProfile,
                    isFollower = true,
                    userProfileVisibility = viewModel.userProfileVisibility,
                    userRecipes = apiSuccess(listOf(
                        RecipeInfo(
                            id = 1,
                            name = "Spaghetti Carbonara",
                            authorUsername = "John Doe",
                            rating = 3.5,
                            cuisine = Cuisine.ITALIAN,
                            mealType = MealType.MAIN_COURSE,
                            preparationTime = 45,
                            servings = 4,
                            picture = ""
                        ),
                        RecipeInfo(
                            id = 2,
                            name = "Chicken Curry",
                            authorUsername = "John Doe",
                            rating = 4.0,
                            cuisine = Cuisine.INDIAN,
                            mealType = MealType.MAIN_COURSE,
                            preparationTime = 30,
                            servings = 2,
                            picture = ""
                        )
                    )),
                    recipeCollectionsState = null,
                    kitchenBookCollectionsState = apiSuccess(listOf(
                        CollectionProfile(1, "Italian Delights"),
                        CollectionProfile(2, "Quick Meals")
                    )),
                    onBackButton = { finish() },
                    onSettingsButton = { navigateTo<SettingsActivity>() },
                    onFollowersButton = { navigateToFollowActivity(0) },
                    onFollowingButton = { navigateToFollowActivity(1) },
                    onFollowRequest = {  },
                    onCollectionRequest = { collectionId ->
                        viewModel.getKitchenBookCollectionRecipes(collectionId)
                    },
                    onCollectionCreate = { },
                    onUserProfileRefresh = {
                        val userProfileName = intent.getStringExtra(Intents.USERNAME) ?: error("Missing USERNAME in intent")
                        viewModel.getUserProfile(userProfileName)
                    },
                    onUserRecipesLoadMore = { viewModel.getUserRecipes() },
                    onUserKitchenBookLoadMore = { viewModel.getUserKitchenBook() },
                    onUserPictureChange = {},
                    userProfileState = userProfile.value,
                    enableButtons = viewModel.enableButtons
                )
            }
        }
    }

    fun navigateToFollowActivity(selectedTab: Int) {
        navigateTo<FollowActivity> { intent ->
            intent.putExtra(Intents.FOLLOW_TAB, selectedTab)
        }
    }

    private fun navigateToKitchenBookActivity(collectionId: Int) {
/*        navigateTo<KitchenBookActivity> { intent ->
            intent.putExtra(Intents.COLLECTION_ID, collectionId)
        }*/
    }
}

data class UserProfileStateBundle(
    val profile: LoadState<UserProfile>,
    val recipes: LoadState<List<RecipeInfo>>,
    val kitchenBook: LoadState<List<CollectionProfile>>,
    val followers: LoadState<List<FollowUser>>,
    val following: LoadState<List<FollowingUser>>
)