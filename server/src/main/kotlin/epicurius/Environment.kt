package epicurius

import java.io.FileInputStream

object Environment {

    fun getFirestoreDatabaseId() = "epicurius-database"

    fun getSpoonacularAPIKey() = FileInputStream("src/main/resources/SpoonacularAPIKey.txt")
}
