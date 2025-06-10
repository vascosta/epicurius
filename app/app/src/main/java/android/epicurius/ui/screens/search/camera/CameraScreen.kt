package android.epicurius.ui.screens.search.camera

import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.search.camera.components.CameraView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onBackButton: () -> Unit,
    onIdentifyIngredients: () -> List<String>,
    onConfirmIngredients: (List<String>) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    Scaffold(
        topBar = { TopBar("Camera", backButton = true, onBackButton, icon = null, buttonsEnable = true) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                cameraPermissionState.status.isGranted -> {
                    CameraView(
                        onIdentifyIngredients = onIdentifyIngredients,
                        onConfirmIngredients = onConfirmIngredients
                    )
                }
                cameraPermissionState.status.shouldShowRationale -> {
                    LaunchedEffect(Unit) {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
                else -> {
                    LaunchedEffect(Unit) {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            }
        }
    }
}
