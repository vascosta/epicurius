package android.epicurius.ui.screens.user.profile

import android.epicurius.domain.collection.CollectionProfile
import android.epicurius.ui.EpicuriusActivity
import android.epicurius.ui.navigation.Intents
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.collections.CollectionsViewModel
import android.epicurius.ui.screens.collections.collection.CollectionActivity
import android.epicurius.ui.screens.collections.favourites.FavouritesActivity
import android.epicurius.ui.screens.collections.recipeCollections.RecipeCollectionsViewModel
import android.epicurius.ui.screens.collections.recipeCollections.components.RecipeCollectionsStateBundle
import android.epicurius.ui.screens.feed.FeedActivity
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.epicurius.ui.screens.user.follow.FollowActivity
import android.epicurius.ui.screens.utils.Idle
import android.epicurius.ui.screens.utils.idle
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import epicurius.domain.collection.CollectionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserProfileActivity : EpicuriusActivity() {
    override val viewModel: UserProfileViewModel by getViewModel<UserProfileViewModel>()
    val collectionsViewModel: CollectionsViewModel by getViewModel<CollectionsViewModel>()
    val recipeCollectionsViewModel: RecipeCollectionsViewModel by getViewModel<RecipeCollectionsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.userProfile.collectLatest { state ->
                val userProfileName = intent.getStringExtra(Intents.USERNAME) ?: viewModel.session.getUserName()
                if (state is Idle) viewModel.getUserProfile(userProfileName)
            }
        }
        setContent {
            val userProfile = viewModel.userProfile.collectAsState()
            val userRecipes = viewModel.userRecipes.collectAsState()
            val userKitchenBook = viewModel.userKitchenBook.collectAsState()
            val collectionsToAddRecipeState = recipeCollectionsViewModel.collectionsToAddRecipe.collectAsState(idle())
            val collectionsToRemoveRecipeState = recipeCollectionsViewModel.collectionsToRemoveRecipe.collectAsState(idle())
            MaterialTheme {
                UserProfileScreen(
                    isAnotherUserProfile = viewModel.isAnotherUserProfile,
                    userProfileVisibility = viewModel.userProfileVisibility,
                    userProfileState = userProfile.value,
                    userRecipesState = userRecipes.value,
                    userKitchenBookState = userKitchenBook.value,
                    recipeCollectionsStateBundle = RecipeCollectionsStateBundle(
                        collectionsToAddRecipeState.value,
                        collectionsToRemoveRecipeState.value
                    ),
                    onBackButton = { finish() },
                    onUpdateUserProfilePicture = { picture: ByteArray? ->
                        viewModel.updateUserProfilePicture(picture)
                    },
                    onFollow = { username: String -> viewModel.follow(username) },
                    onUnfollow = { username: String -> viewModel.unfollow(username) },
                    onCancelFollow = { username: String -> viewModel.cancelFollow(username) },
                    onUserKitchenBookCollectionCreate = { collectionName: String, username: String ->
                        collectionsViewModel.createCollection(collectionName, CollectionType.KITCHEN_BOOK) {
                            collectionId: Int -> navigateToCollectionActivity(collectionId, true)
                        }
                    },
                    onUserKitchenBookCollectionDelete = { collectionId: Int ->
                        collectionsViewModel.deleteCollection(collectionId)
                    },
                    onAddRecipeToCollections = {
                            recipeId: Int,
                            collectionsToAdd: List<CollectionProfile>
                        ->
                        recipeCollectionsViewModel.addRecipeToCollections(
                            recipeId,
                            collectionsToAdd
                        )
                    },
                    onRemoveRecipeFromCollections = {
                            recipeId: Int,
                            collectionsToRemove: List<CollectionProfile>
                        ->
                        recipeCollectionsViewModel.removeRecipeFromCollections(
                            recipeId,
                            collectionsToRemove
                        )
                    },
                    onUserRecipesClear = { viewModel.clearUserRecipes() },
                    onUserKitchenBookClear = { viewModel.clearUserKitchenBook() },
                    onRecipeCollectionsClear = { recipeCollectionsViewModel.clearRecipeCollections() },
                    onUserRecipesRequest = { username : String? -> viewModel.getUserRecipes(username) },
                    onUserKitchenBookRequest = { username: String? -> viewModel.getUserKitchenBook(username) },
                    onFollowersOrFollowingRequest = ::navigateToFollowActivity,
                    onUserKitchenBookCollectionRequest = ::navigateToCollectionActivity,
                    onRecipeCollectionsRequest = { recipeId: Int ->
                        val collectionType = if (viewModel.isAnotherUserProfile) CollectionType.FAVOURITE
                        else CollectionType.KITCHEN_BOOK
                        recipeCollectionsViewModel.getRecipeCollections(recipeId, collectionType)
                    },
                    onRecipeRequest = ::navigateToRecipeProfileActivity,
                    enableButtons = viewModel.enableButtons && collectionsViewModel.enableButtons &&
                            recipeCollectionsViewModel.enableButtons
                )
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        lifecycleScope.launch {
            val userProfileName = intent.getStringExtra(Intents.USERNAME) ?: viewModel.session.getUserName()
            viewModel.getUserProfile(userProfileName)
            if (viewModel.userProfileVisibility) {
                viewModel.clearUserRecipes()
                viewModel.clearUserKitchenBook()
                viewModel.getUserRecipes(userProfileName)
                viewModel.getUserKitchenBook(userProfileName)
            }
        }
    }

    private fun navigateToFollowActivity(
        selectedTab: Int,
        username: String,
        followersCount: Int,
        followingCount: Int
    ) {
        navigateTo<FollowActivity> { intent ->
            intent.putExtra(Intents.FOLLOW_TAB, selectedTab)
            intent.putExtra(Intents.USERNAME, username)
            intent.putExtra(Intents.FOLLOWERS_COUNT, followersCount)
            intent.putExtra(Intents.FOLLOWING_COUNT, followingCount)
        }
    }

    private fun navigateToCollectionActivity(
        collectionId: Int,
        isCollectionOwner: Boolean
    ) {
        navigateTo<CollectionActivity> { intent ->
            intent.putExtra(Intents.COLLECTION_ID, collectionId)
            intent.putExtra(Intents.IS_COLLECTION_OWNER, isCollectionOwner)
        }
    }

    private fun navigateToRecipeProfileActivity(recipeId: Int) {
        navigateTo<RecipeProfileActivity> { intent ->
            intent.putExtra(Intents.RECIPE_ID, recipeId)
        }
    }
}