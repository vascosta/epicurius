package android.epicurius.ui.screens.search.camera.components

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.io.File

fun takePhotoToGallery(
    imageCapture: ImageCapture?,
    context: Context,
    onIdentifyIngredients: (pictureBytes: ByteArray) -> Unit
) {
    if (imageCapture == null) return

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Epicurius")
    }

    val contentResolver = context.contentResolver

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Error taking picture", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri
                if (savedUri != null) {
                    try {
                        val inputStream = contentResolver.openInputStream(savedUri)
                        val byteArray = inputStream?.readBytes()
                        inputStream?.close()

                        if (byteArray != null) {
                            onIdentifyIngredients(byteArray)
                        } else {
                            Toast.makeText(context, "Error reading image bytes", Toast.LENGTH_SHORT).show()
                        }

                        Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error on saving image to gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )
}


