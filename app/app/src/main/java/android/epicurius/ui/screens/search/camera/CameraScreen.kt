package android.epicurius.ui.screens.search.camera

import android.content.Intent
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.search.camera.components.CameraView
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onTakePicture: (List<String>) -> Unit = {},
    onBackButton: () -> Unit = {}
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val context = LocalContext.current

    Scaffold(
        topBar = { TopBar("Camera", backButton = true, onBackButton, icon = null) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                cameraPermissionState.status.isGranted -> {
                    CameraView(onTakePicture)
                }
                cameraPermissionState.status.shouldShowRationale -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Need access to the camera to continue")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text("Allow Camera Access")
                        }
                    }
                }
                !cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Permission denied. You can enable it in settings.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }) {
                            Text("Open Settings")
                        }
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
