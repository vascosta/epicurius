package epicurius.unit.services.user

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.User
import epicurius.unit.services.ServiceTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class UserServiceTest : ServiceTest() {

    val testUsername = generateRandomUsername()
    val testUser = User(
        1,
        testUsername,
        generateEmail(testUsername),
        userDomain.encodePassword(randomUUID().toString()),
        userDomain.hashToken(randomUUID().toString()),
        "PT",
        false,
        listOf(Intolerance.GLUTEN),
        listOf(Diet.GLUTEN_FREE),
        randomUUID().toString()
    )

    val testUsername2 = generateRandomUsername()
    val testUser2 = User(
        2,
        testUsername2,
        generateEmail(testUsername2),
        userDomain.encodePassword(randomUUID().toString()),
        userDomain.hashToken(randomUUID().toString()),
        "PT",
        false,
        listOf(Intolerance.GLUTEN),
        listOf(Diet.GLUTEN_FREE),
        randomUUID().toString()
    )

}
