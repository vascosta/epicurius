package epicurius.unit.http.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class UserHttpTest : HttpTest() {

    companion object {
        val publicTestUsername = generateRandomUsername()
        private val publicTestUserToken = randomUUID().toString()
        val publicTestUser = AuthenticatedUser(
            User(
                1,
                publicTestUsername,
                generateEmail(publicTestUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(publicTestUserToken),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            publicTestUserToken
        )

        val privateTestUsername = generateRandomUsername()
        private val privateTestUserToken = randomUUID().toString()
        val privateTestUser = AuthenticatedUser(
            User(
                2,
                privateTestUsername,
                generateEmail(privateTestUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(privateTestUserToken),
                "PT",
                true,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString(),
            ),
            privateTestUserToken
        )
    }
}
