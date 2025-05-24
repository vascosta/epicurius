package android.epicurius

import android.epicurius.ui.screens.auth.login.LoginScreen
import android.epicurius.ui.screens.auth.signup.SignUpScreen
import android.epicurius.ui.screens.recipe.profile.RecipeProfileScreen
import android.epicurius.ui.screens.settings.SettingsScreens
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.epicurius.ui.theme.AppTheme
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            MaterialTheme {
                LoginScreen()
            }
        }
    }
}