package android.epicurius.ui.screens.search.camera.components

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.io.File

// Save the photo to a specific directory in the cache
fun takePhoto(
    imageCapture: ImageCapture?,
    context: Context
) {
    val outputDirectory = File(context.cacheDir, "photos").also { it.mkdirs() }

    val file = File(
        outputDirectory,
        "${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Error taking picture: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Picture saved: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

fun takePhotoToGallery(
    imageCapture: ImageCapture?,
    context: Context
) {
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

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context, "Error taking picture: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Picture saved on gallery!", Toast.LENGTH_SHORT).show()
            }
        }
    )
}


