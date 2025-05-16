package android.epicurius.services.http.utils

@JvmInline
value class APIResult<out T>(val value: Any?) {

    val isSuccess get() = value !is Failure
    val isFailure get() = value is Failure

    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    fun getOrThrow(): T =
        when {
            isFailure -> error("Result is failure")
            else -> value as T
        }

    fun problemOrNull(): Problem? =
        when (value) {
            is Failure -> value.problem
            else -> null
        }

    fun problemOrThrow(): Problem =
        when (value) {
            is Failure -> value.problem
            else -> error("Result is not failure")
        }

    companion object {
        fun <T> success(value: T): APIResult<T> = APIResult(value)
        fun <T> failure(problem: Problem): APIResult<T> = APIResult(Failure(problem))
    }

    data class Failure(val problem: Problem)
}
