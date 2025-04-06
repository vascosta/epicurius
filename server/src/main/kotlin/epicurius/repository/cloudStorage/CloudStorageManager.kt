package epicurius.repository.cloudStorage

import com.google.cloud.storage.Storage
import epicurius.repository.cloudStorage.picture.PictureCloudStorageRepository
import org.springframework.stereotype.Component

@Component
class CloudStorageManager(cloudStorage: Storage) {
    val pictureCloudStorageRepository = PictureCloudStorageRepository(cloudStorage)
}
