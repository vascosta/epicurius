package epicurius.unit.http.feed

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.RecipeInfo
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.feed.models.output.FeedOutputModel
import epicurius.unit.http.HttpTest
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID.randomUUID
import kotlin.test.Test

class GetFeedHttpTests: HttpTest() {

    @Test
    fun `Should retrieve user's empty feed`() {
        // given a user that follows no users and pagination params
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, pagingParams)
        ).thenReturn(emptyList())

        // when retrieving the feed
        val response = getFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should be empty
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FeedOutputModel(emptyList()), response.body)
    }

    @Test
    fun `Should retrieve user's feed with recipes`() {
        // given a user that follows another user with recipe and pagination params
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, pagingParams)
        ).thenReturn(listOf(recipeInfo))

        // when retrieving the feed
        val response = getFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FeedOutputModel(listOf(recipeInfo)), response.body)
    }

    @Test
    fun `Should retrieve user's feed order by most recent recipe`() {
        // given a user that follows another user with recipe and pagination params
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, pagingParams)
        ).thenReturn(listOf(recipeInfo2, recipeInfo))

        // when retrieving the feed
        val response = getFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FeedOutputModel(listOf(recipeInfo2, recipeInfo)), response.body)
    }

    companion object {
        private val authenticatedUsername = generateRandomUsername()
        private val token = randomUUID().toString()

        private val testAuthenticatedUser = AuthenticatedUser(
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

        private val recipeInfo = RecipeInfo(
            id = 1,
            name = "Carbonara",
            cuisine = Cuisine.ITALIAN,
            mealType = MealType.MAIN_COURSE,
            preparationTime = 30,
            servings = 4,
            picture = ByteArray(0)
        )

        private val recipeInfo2 = RecipeInfo(
            id = 2,
            name = "Spring Rolls",
            cuisine = Cuisine.CHINESE,
            mealType = MealType.APPETIZER,
            preparationTime = 20,
            servings = 2,
            picture = ByteArray(0)
        )
    }
}