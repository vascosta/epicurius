package android.epicurius.services.http

import android.epicurius.domain.exceptions.InvalidResponseException
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Problem
import android.epicurius.services.http.utils.authorizationHeader
import android.epicurius.services.http.utils.getBodyOrThrow
import android.epicurius.services.http.utils.isApplicationJson
import android.epicurius.services.http.utils.isFailure
import android.epicurius.services.http.utils.isProblem
import android.epicurius.services.http.utils.params
import android.epicurius.services.http.utils.toJsonBody
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HttpService(
    val baseUrl: String,
    val client: OkHttpClient,
    val gson: Gson
) {
    suspend inline fun <reified T> get(
        endpoint: String,
        params: Map<String, Any?>? = null,
        token: String? = null
    ): APIResult<T> =
        Request.Builder()
            .url(baseUrl + endpoint.params(params))
            .authorizationHeader(token)
            //.userAgentHeader()
            .build()
            .getResponseResult()

    suspend inline fun <reified T> post(
        endpoint: String,
        body: Any? = null,
        params: Map<String, Any?>? = null,
        token: String? = null
    ): APIResult<T> =
        Request.Builder()
            .url(baseUrl + endpoint.params(params))
            .authorizationHeader(token)
            //.userAgentHeader()
            .post(gson.toJsonBody(body))
            .build()
            .getResponseResult()

    suspend inline fun <reified T> patch(
        endpoint: String,
        body: Any? = null,
        params: Map<String, Any?>? = null,
        token: String? = null
    ): APIResult<T> =
        Request.Builder()
            .url(baseUrl + endpoint.params(params))
            .authorizationHeader(token)
            //.userAgentHeader()
            .patch(gson.toJsonBody(body))
            .build()
            .getResponseResult()

    suspend inline fun <reified T> put(
        endpoint: String,
        body: Any? = null,
        params: Map<String, Any?>? = null,
        token: String? = null
    ): APIResult<T> =
        Request.Builder()
            .url(baseUrl + endpoint.params(params))
            .authorizationHeader(token)
            //.userAgentHeader()
            .put(gson.toJsonBody(body))
            .build()
            .getResponseResult()

    suspend inline fun <reified T> delete(
        endpoint: String,
        params: Map<String, Any?>? = null,
        token: String? = null
    ): APIResult<T> =
        Request.Builder()
            .url(baseUrl + endpoint.params(params))
            .authorizationHeader(token)
            //.userAgentHeader()
            .delete()
            .build()
            .getResponseResult()

    suspend fun <T> Request.send(client: OkHttpClient, handler: (Response) -> T): T =
        suspendCancellableCoroutine { cont ->
            client.newCall(this).let { call ->
                call.enqueue(
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            cont.resumeWithException(e)
                        }
                        override fun onResponse(call: Call, response: Response) {
                            try {
                                cont.resume(handler(response))
                            } catch (th: Throwable) {
                                cont.resumeWithException(th)
                            }
                        }
                    }
                )
                cont.invokeOnCancellation { call.cancel() }
            }
        }

    suspend inline fun <reified T> Request.getResponseResult(): APIResult<T> =
        send(client) { res ->
            val body = res.getBodyOrThrow()
            val json = JsonReader(body.charStream())
            when {
                res.isSuccessful && body.isApplicationJson -> {
                    val jsonBody = gson.fromJson<T>(json, T::class.java)
                    APIResult.success(jsonBody)
                }
                res.isFailure && body.isProblem -> {
                    val problem = gson.fromJson<Problem>(json, Problem::class.java)
                    APIResult.failure(problem)
                }
                else -> when (res.code) {
                    BAD_GATEWAY -> throw InvalidResponseException("Could not connect to server")
                    else -> throw InvalidResponseException("Invalid server response")
                }
            }
        }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        //const val USER_AGENT_HEADER = "User-Agent"
        const val TOKEN_TYPE = "Bearer"
        const val BAD_GATEWAY = 502
    }
}