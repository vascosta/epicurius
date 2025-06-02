package android.epicurius.services.http.media

import okhttp3.MediaType.Companion.toMediaType

object MediaTypes {
    val applicationJsonMediaType = "application/json".toMediaType()
    val problemMediaType = "application/problem+json".toMediaType()
}