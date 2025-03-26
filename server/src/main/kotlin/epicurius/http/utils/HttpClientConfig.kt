package epicurius.http.utils

interface HttpClientConfig {
    suspend fun get(uri: String): String
}