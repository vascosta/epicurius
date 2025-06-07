package android.epicurius.services.http.utils

import android.epicurius.services.http.media.Problem

class APIResult<out T>(private val value: Any?, private val token: String? = null) {

    val isSuccess get() = value !is Failure
    val isFailure get() = value is Failure

    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    fun getValueOrThrow(): T =
        when {
            isFailure -> error("Result is failure")
            else -> value as T
        }

    fun getTokenOrThrow(): String = token ?: error("Token not in result")

    /*fun getProblemOrNull(): Problem? =
        when (value) {
            is Failure -> value.problem
            else -> null
        }*/

    fun getProblemOrThrow(): Problem =
        when (value) {
            is Failure -> value.problem
            else -> error("Result is not failure")
        }

    companion object {
        fun <T> success(value: T?, token: String?): APIResult<T> = APIResult(value, token)
        fun <T> cached(value: T?, token: String?): CachedResult<T> = CachedResult(value, token)
        fun <T> failure(problem: Problem): APIResult<T> = APIResult(Failure(problem))
    }

    data class Failure(val problem: Problem)
}
