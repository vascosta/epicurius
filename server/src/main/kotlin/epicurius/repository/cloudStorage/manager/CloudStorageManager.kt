package epicurius.repository.cloudStorage.manager

import epicurius.config.CloudStorage
import epicurius.repository.cloudStorage.picture.PictureRepository
import org.springframework.stereotype.Component

@Component
class CloudStorageManager(cloudStorage: CloudStorage) {
    val pictureRepository = PictureRepository(cloudStorage)
}
