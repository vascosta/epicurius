package epicurius.unit.services.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.User
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class UserServiceTest : ServiceTest() {

    val publicTestUsername = generateRandomUsername()
    val publicTestUser = User(
        1,
        publicTestUsername,
        generateEmail(publicTestUsername),
        userDomain.encodePassword(randomUUID().toString()),
        userDomain.hashToken(randomUUID().toString()),
        "PT",
        false,
        listOf(Intolerance.GLUTEN),
        listOf(Diet.GLUTEN_FREE),
        randomUUID().toString()
    )

    val privateTestUsername = generateRandomUsername()
    val privateTestUser = User(
        2,
        privateTestUsername,
        generateEmail(privateTestUsername),
        userDomain.encodePassword(randomUUID().toString()),
        userDomain.hashToken(randomUUID().toString()),
        "PT",
        true,
        listOf(Intolerance.GLUTEN),
        listOf(Diet.GLUTEN_FREE),
        randomUUID().toString()
    )
}
