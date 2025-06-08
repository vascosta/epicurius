package android.epicurius.ui.screens.search.camera

import android.epicurius.ui.screens.search.general.SearchActivity
import android.epicurius.ui.navigation.navigateTo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraScreen(
                onBackButton = { navigateTo<SearchActivity>() }
            )
        }
    }
}
