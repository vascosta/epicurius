package android.epicurius.services.http.utils

import java.net.URI

data class Problem(
    val type: URI,
    val title: String,
    val detail: String? = null,
    val instance: URI? = null
)
