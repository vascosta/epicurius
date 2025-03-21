package epicurius.repository

interface UserCloudStorageRepository {
    fun addProfilePicture(username: String, profilePicture: String) // change to jpg or something else
    fun getProfilePicture(username: String) // change to jpg or something else
    fun updateProfilePicture(username: String, profilePicture: String) // change to jpg or something else
    fun removeProfilePicture(username: String) // change to jpg or something else
}