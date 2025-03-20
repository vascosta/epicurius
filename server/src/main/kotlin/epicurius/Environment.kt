package epicurius

import java.io.FileInputStream

object Environment {
    private const val POSTGRES_DATABASE_URL = "POSTGRES_DATABASE_URL"
    private const val FIRESTORE_DATABASE_ID = "FIRESTORE_DATABASE_ID"
    private const val FIRESTORE_DATABASE_TEST_ID = "FIRESTORE_DATABASE_TEST_ID"
    private const val GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS"

    fun getFirestoreDatabaseId() = "epicurius-database"

    fun getFirestoreDatabaseTestId() = "epicurius-database-test"

    //fun getPostgresDbUrl() = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"
    fun getPostgresDbUrl() = "jdbc:postgresql://localhost/postgres?user=postgres&password=PS"
    //System.getenv(POSTGRES_DATABASE_URL) ?: throw Exception("Missing environment variable 'POSTGRES_DATABASE_URL'")

    fun getFirestoreServiceAccount() = FileInputStream("src/main/resources/epicurius-credentials.json")
        //System.getenv(GOOGLE_APPLICATION_CREDENTIALS) ?: throw Exception("Missing environment variable '$GOOGLE_APPLICATION_CREDENTIALS'")
}