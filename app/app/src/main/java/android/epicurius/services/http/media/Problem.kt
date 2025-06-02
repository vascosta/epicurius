package android.epicurius.services.http.media

import java.net.URI

data class Problem(
    val type: URI? = null,
    val title: String? = null,
    val detail: String,
    val instance: URI? = null
)