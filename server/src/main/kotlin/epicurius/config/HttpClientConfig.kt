package epicurius.config

interface HttpClientConfig {
    suspend fun get(uri: String): String
}
