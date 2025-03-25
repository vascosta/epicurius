package epicurius.repository.cloudStorage

import com.google.cloud.storage.Storage
import epicurius.repository.cloudStorage.UserCloudStorageRepository
import org.springframework.stereotype.Component

@Component
class CloudStorageManager(cloudStorage: Storage) {
    val userCloudStorageRepository = UserCloudStorageRepository(cloudStorage)
}