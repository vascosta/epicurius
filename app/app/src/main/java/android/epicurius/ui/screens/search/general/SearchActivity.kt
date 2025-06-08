package android.epicurius.ui.screens.search.general

import android.epicurius.ui.screens.search.camera.CameraActivity
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(
                onBackButton = {  },
                onCamera = { navigateTo<CameraActivity>() }
            )
        }
    }
}