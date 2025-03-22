package epicurius.repository

import epicurius.EpicuriusTest
import epicurius.services.models.UpdateUserModel
import epicurius.utils.UserTest
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeAll

open class RepositoryTest: EpicuriusTest() {

    companion object {
        lateinit var publicTestUser: UserTest
        lateinit var privateTestUser: UserTest

        @JvmStatic
        @BeforeAll
        fun setupDB() {
            publicTestUser = createTestUser(tm, fs, false)
            privateTestUser = createTestUser(tm, fs, true)
        }

        fun createUser(username: String, email: String, country: String, passwordHash: String) =
            tm.run { it.userRepository.createUser(username, email, country, passwordHash) }

        fun createUserFollowersAndFollowing(username: String, privacy: Boolean) =
            fs.userRepository.createUserFollowersAndFollowing(username, privacy)

        fun getUserByName(username: String) = tm.run { it.userRepository.getUser(username, null) }

        fun getUserByEmail(email: String) = tm.run { it.userRepository.getUser(null, email) }

        fun resetPassword(email: String, passwordHash: String) =
            tm.run { it.userRepository.resetPassword(email, passwordHash) }

        fun updateProfile(username: String, userUpdate: UpdateUserModel) =
            tm.run {
                it.userRepository.updateProfile(
                    username,
                    UpdateUserModel(
                        userUpdate.username,
                        userUpdate.email,
                        userUpdate.country,
                        userUpdate.passwordHash,
                        userUpdate.privacy,
                        userUpdate.intolerances,
                        userUpdate.diet
                    )
                )
            }
    }
}