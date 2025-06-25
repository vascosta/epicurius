package android.epicurius.ui.screens.search.camera.components

import android.epicurius.ui.screens.search.components.ConfirmIngredientsDialog
import android.epicurius.ui.screens.utils.LoadState
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraView(
    ingredientsState: LoadState<List<String>>,
    onIdentifyIngredients: (ByteArray) -> Unit,
    onConfirmIngredients: (List<String>) -> Unit,
    onIngredientsClear: () -> Unit,
    enableButtons: Boolean,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }

    val cameraProvider = remember { ProcessCameraProvider.getInstance(context) }

    var showConfirmIngredientsDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProvider.addListener({
                    val cameraProvider = cameraProvider.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val capture = ImageCapture.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )

                        imageCapture.value = capture
                    } catch (exc: Exception) {
                        exc.printStackTrace()
                    }
                }, executor)

                previewView
            }
        )
        Button(
            onClick = {
                takePhotoToGallery(
                    imageCapture = imageCapture.value,
                    context = context,
                    onIdentifyIngredients = { bytes ->
                        onIdentifyIngredients(bytes)
                        showConfirmIngredientsDialog = true
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) { Text("Take Picture") }
    }

    if (showConfirmIngredientsDialog) {
        ConfirmIngredientsDialog(
            ingredientsState = ingredientsState,
            onConfirmIngredients = onConfirmIngredients,
            onIngredientsClear = onIngredientsClear,
            onCloseDialog = { showConfirmIngredientsDialog = false },
            enableButtons = enableButtons
        )
    }
}
