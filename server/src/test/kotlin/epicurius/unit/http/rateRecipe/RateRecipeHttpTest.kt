package epicurius.unit.http.rateRecipe

import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import java.util.UUID.randomUUID

open class RateRecipeHttpTest : HttpTest() {

    companion object {

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
                emptyList(),
                emptyList(),
                randomUUID().toString()
            ),
            token,
        )

        private val authorAuthenticatedUsername = generateRandomUsername()
        private val authorToken = randomUUID().toString()

        val testAuthorAuthenticatedUser = AuthenticatedUser(
            User(
                2,
                authorAuthenticatedUsername,
                generateEmail(authorAuthenticatedUsername),
                userDomain.encodePassword(randomUUID().toString()),
                userDomain.hashToken(authorToken),
                "PT",
                false,
                emptyList(),
                emptyList(),
                randomUUID().toString()
            ),
            authorToken,
        )

        const val RECIPE_ID = 1
        const val RATING_5 = 5
        const val RATING_3 = 3
    }
}
