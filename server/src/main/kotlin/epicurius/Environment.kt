package epicurius

object Environment {
    private const val DATABASE_URL = "DATABASE_URL"

    fun getDbUrl() = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"//System.getenv(DATABASE_URL) ?: throw Exception("Missing environment variable '$DATABASE_URL'")
}