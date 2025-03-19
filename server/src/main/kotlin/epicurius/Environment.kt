package epicurius

import java.io.FileInputStream

object Environment {
    private const val DATABASE_URL = "DATABASE_URL"
    private const val GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS"

    //fun getDbUrl() = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"
    fun getDbUrl() = "jdbc:postgresql://localhost/postgres?user=postgres&password=PS"
    //System.getenv(DATABASE_URL) ?: throw Exception("Missing environment variable '$DATABASE_URL'")
    fun getFirestoreServiceAccount() = FileInputStream("src/main/resources/epicurius-credentials.json")
        //System.getenv(GOOGLE_APPLICATION_CREDENTIALS) ?: throw Exception("Missing environment variable '$GOOGLE_APPLICATION_CREDENTIALS'")
}