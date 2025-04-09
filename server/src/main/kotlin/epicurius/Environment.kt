package epicurius

import java.io.FileInputStream

object Environment {

    fun getFirestoreDatabaseId() = "epicurius-database"

    fun getCloudStorageBucketName() = "epicurius-bucket"

    fun getSpoonacularAPIKey() = FileInputStream("src/main/resources/SpoonacularAPIKey.txt")
}
