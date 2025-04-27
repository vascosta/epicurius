package epicurius.unit.http.recipe

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class RecipeHttpTest : HttpTest() {

    companion object {
        const val RECIPE_ID = 1

        val objectMapper = jacksonObjectMapper()

        private val authenticatedUsername = generateRandomUsername()
        private val token = randomUUID().toString()

        val testAuthenticatedUser = AuthenticatedUser(
            User(
                1,
                authenticatedUsername,
                generateEmail(authenticatedUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(token),
                "PT",
                false,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.GLUTEN_FREE),
                randomUUID().toString()
            ),
            token,
        )

        val recipePictures = setOf(testPicture)
    }
}
