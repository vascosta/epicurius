package android.epicurius.services.http.utils

import android.epicurius.services.http.HttpService.Companion.AUTHORIZATION_HEADER
import android.epicurius.services.http.HttpService.Companion.TOKEN_TYPE
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

val Response.isFailure get() = !isSuccessful
val ResponseBody.isApplicationJson get() = contentType() == MediaTypes.applicationJsonMediaType
val ResponseBody.isProblem get() = contentType() == MediaTypes.problemMediaType

fun Response.getBodyOrThrow(): ResponseBody = body
    ?: throw IllegalArgumentException("Response body is null")

fun Request.Builder.authorizationHeader(token: String?) =
    token?.let { header(AUTHORIZATION_HEADER, "$TOKEN_TYPE $it") } ?: this

//fun Request.Builder.userAgentHeader() = header(USER_AGENT_HEADER, "Android")

fun String.addQueryParams(vararg params: Map<String, Any?>): String = params
    .flatMap { it.entries }
    .filter { it.value != null }
    .joinToString("&") { "${it.key}=${it.value}" }
    .let { if (it.isEmpty()) this else "$this?$it" }

fun String.params(params: Map<String, Any?>?): String = params?.let { addQueryParams(it) } ?: this