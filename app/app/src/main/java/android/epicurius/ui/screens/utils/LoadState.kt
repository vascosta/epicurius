package android.epicurius.ui.screens.utils

import android.epicurius.services.http.media.Problem
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.CachedResult

/**
 * Sum type that represents the state of a load operation.
 */
sealed class LoadState<out T>

/**
 * The idle state, i.e. the state before the load operation is started.
 */
object Idle : LoadState<Nothing>()

/**
 * The loading state, i.e. the state while the load operation is in progress.
 */
data class Loading<T>(val cachedValue: CachedResult<T>?): LoadState<T>()

/**
 * The loaded state, i.e. the state after the load operation has finished.
 * @param value the result of the load operation.
 */
data class Loaded<T>(val value: APIResult<T>) : LoadState<T>()

typealias Cached<T> = Loaded<T>

/**
 * Returns a new [LoadState] in the idle state.
 */
fun idle(): Idle = Idle

/**
 * Returns a new [LoadState] in the loading state.
 */
fun <T> loading(cachedValue: CachedResult<T>? = null): Loading<T> = Loading(cachedValue)

/**
 * Returns a new [LoadState] in the loaded state.
 */
fun <T> loaded(value: APIResult<T>): Loaded<T> = Loaded(value)

/**
 * Returns a new [LoadState] in the cached state with the provided value.
 */
fun <T> cached(value: APIResult<T>): Cached<T> = Cached(value)

/**
 * Returns a new [LoadState] in the loaded state with a successful result.
 */
fun <T> apiSuccess(value: T, token: String? = null): Loaded<T> = loaded(APIResult.success(value, token))

fun <T> cache(value: T, token: String? = null): Cached<T> = cached(CachedResult.cached(value, token))

/**
 * Returns a new [LoadState] in the loaded state with a failed result.
 */
fun <T> apiFailure(problem: Problem): Loaded<T> = loaded(APIResult.failure(problem))

/**
 * Returns the result of the load operation, if one is available
 * If the load operation is still in progress, an [IllegalStateException] is thrown.
 */
fun <T> LoadState<T>.getOrThrow(): T = when (this) {
    is Loaded -> value.getValueOrThrow()
    is Loading -> cachedValue?.getValueOrThrow() ?: throw IllegalStateException("No value available")
    else -> throw IllegalStateException("No value available")
}

/**
 * Returns the result of the load operation, if one is available.
 */
fun <T> LoadState<T>.getOrNull(): T? = when (this) {
    is Loaded -> value.getValueOrNull()
    else -> null
}

/**
 * Returns the problem that caused the load operation to fail, if one is available.
 * If the load operation is still in progress, an [IllegalStateException] is thrown.
 */
fun <T> LoadState<T>.problemOrThrow(): Problem = when (this) {
    is Loaded -> value.getProblemOrThrow()
    else -> throw IllegalStateException("No problem available")
}