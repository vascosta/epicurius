package epicurius.config

interface HttpClientConfig {
    suspend fun get(uri: String): String
    suspend fun post(uri: String, body: Map<String, String>): String
}
