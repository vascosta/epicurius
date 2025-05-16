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

fun String.addPathParams(vararg params: Map<String, Any?>): String = params
    .flatMap { it.entries }
    .fold(this) { acc, (key, value) ->
        if (value != null) acc.replace("{$key}", value.toString()) else acc
    }

fun String.addQueryParams(vararg params: Map<String, Any?>): String = params
    .flatMap { it.entries }
    .filter { it.value != null }
    .joinToString("&") { "${it.key}=${it.value}" }
    .let { if (it.isEmpty()) this else "$this?$it" }

fun String.params(
    pathParams: Map<String, Any?>?,
    queryParams: Map<String, Any?>?
): String =
    this
        .let { if (pathParams != null) it.addPathParams(pathParams) else it }
        .let { if (queryParams != null) it.addQueryParams(queryParams) else it }