package epicurius.config

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage

class CloudStorage(val storage: Storage, private val bucketName: String) {
    fun getBlob(objectName: String): Blob = storage.get(bucketName, objectName)

    fun createBlobId(objectName: String): BlobId = BlobId.of(bucketName, objectName)

    fun createBlobInfo(blobId: BlobId, contentType: String): BlobInfo =
        BlobInfo.newBuilder(blobId).setContentType(contentType).build()
}
