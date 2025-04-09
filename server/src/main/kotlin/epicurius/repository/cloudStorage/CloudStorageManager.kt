package epicurius.repository.cloudStorage

import epicurius.config.CloudStorage
import epicurius.repository.cloudStorage.picture.PictureCloudStorageRepository
import org.springframework.stereotype.Component

@Component
class CloudStorageManager(cloudStorage: CloudStorage) {
    val pictureCloudStorageRepository = PictureCloudStorageRepository(cloudStorage)
}
