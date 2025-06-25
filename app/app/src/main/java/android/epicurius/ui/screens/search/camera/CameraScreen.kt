package android.epicurius.ui.screens.search.camera

import android.Manifest
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.search.camera.components.CameraView
import android.epicurius.ui.screens.utils.LoadState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    ingredientsState: LoadState<List<String>>,
    onBackButton: () -> Unit,
    onIdentifyIngredients: (pictureBytes: ByteArray) -> Unit,
    onConfirmIngredients: (ingredients: List<String>) -> Unit,
    onIngredientsClear: () -> Unit,
    enableButtons: Boolean
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Camera",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = enableButtons
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons) },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when {
                    cameraPermissionState.status.isGranted -> {
                        CameraView(
                            ingredientsState = ingredientsState,
                            onIdentifyIngredients = onIdentifyIngredients,
                            onConfirmIngredients = onConfirmIngredients,
                            onIngredientsClear = onIngredientsClear,
                            enableButtons = enableButtons
                        )
                    }

                    else -> LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
                }
            }
        },
        containerColor = Color.White
    )
}
