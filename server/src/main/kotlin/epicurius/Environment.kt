package epicurius

import java.io.FileInputStream

object Environment {

    fun getFirestoreDatabaseId() = "epicurius-database"

    fun getFirestoreDatabaseTestId() = "epicurius-database-test"

    fun getSpoonacularAPIKey() = FileInputStream("src/main/resources/SpoonacularAPIKey.txt")
}
