package android.epicurius.ui.screens.recipe.profile.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

@Composable
fun generateTestImageByteArray(@DrawableRes drawableRes: Int): ByteArray {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
