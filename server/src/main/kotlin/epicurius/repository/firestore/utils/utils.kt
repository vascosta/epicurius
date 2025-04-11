package epicurius.repository.firestore.utils

import com.google.api.core.ApiFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("UNCHECKED_CAST")
fun <T> getMap(firestoreMap: Any?): Map<T, T> {
    return if (firestoreMap is Map<*, *>) {
        try {
            firestoreMap as Map<T, T>
        } catch (e: ClassCastException) {
            emptyMap()
        }
    } else {
        emptyMap()
    }
}

suspend fun <T> ApiFuture<T>.await(): T = suspendCancellableCoroutine { continuation ->
    this.addListener(
        {
            try {
                continuation.resume(this.get())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }, { command -> Thread(command).start() }
    )
}