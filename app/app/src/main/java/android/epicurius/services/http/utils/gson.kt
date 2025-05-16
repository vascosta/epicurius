package android.epicurius.services.http.utils

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST

inline fun <reified T> Gson.fromJson(json: JsonReader): T = fromJson(json, T::class.java)

fun Gson.toJsonBody(body: Any?): RequestBody = body?.let {
    toJson(body).toRequestBody(MediaTypes.applicationJsonMediaType)
} ?: EMPTY_REQUEST