package android.epicurius.ui.screens.search.general

import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.screens.search.camera.CameraActivity
import android.epicurius.ui.navigation.navigateTo
import android.epicurius.ui.screens.recipe.profile.RecipeProfileActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(
                onBackButton = {  },
                onRecipeSearch = { navigateTo<RecipeProfileActivity>() },
                onUserSearch = { listOf<SearchUser>(
                    SearchUser(
                        id = 1,
                        name = "testuser",
                        profilePicture = null,
                    )
                ) },
                onCamera = { navigateTo<CameraActivity>() },
                onUpload = {},
                enableButtons = true,
            )
        }
    }
}