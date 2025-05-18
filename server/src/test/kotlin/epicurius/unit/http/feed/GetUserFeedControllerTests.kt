package epicurius.unit.http.feed

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.User
import epicurius.http.controllers.feed.models.output.GetUserFeedOutputModel
import epicurius.utils.generateEmail
import epicurius.utils.generateRandomUsername
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.UUID.randomUUID
import kotlin.test.Test

class GetUserFeedControllerTests : FeedHttpTest() {

    private val pagingParams = PagingParams(0, 10)
    private val testAuthenticatedUser = AuthenticatedUser(
        User(
            1,
            generateRandomUsername(),
            generateEmail(generateRandomUsername()),
            userDomain.encodePassword(randomUUID().toString()),
            userDomain.hashToken(token),
            "PT",
            false,
            emptyList(),
            emptyList(),
            randomUUID().toString()
        ),
        token = token
    )

    @Test
    fun `Should retrieve user's empty feed`() {
        // given a user (testAuthenticatedUser) that follows no users and pagination params (pagingParams

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, emptyList(), emptyList(), pagingParams)
        ).thenReturn(emptyList())

        // when retrieving the feed
        val response = getUserFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should be empty
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserFeedOutputModel(emptyList()), response.body)
    }

    @Test
    fun `Should retrieve user's feed with recipes`() {
        // given a user (testAuthenticatedUser) that follows another user with recipe and pagination params (pagingParams)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, emptyList(), emptyList(), pagingParams)
        ).thenReturn(listOf(recipeInfo))

        // when retrieving the feed
        val response = getUserFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserFeedOutputModel(listOf(recipeInfo)), response.body)
    }

    @Test
    fun `Should retrieve user's feed order by most recent recipe`() {
        // given a user (testAuthenticatedUser) that follows another user with recipe and pagination params (pagingParams)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, emptyList(), emptyList(), pagingParams)
        ).thenReturn(listOf(recipeInfo2, recipeInfo))

        // when retrieving the feed
        val response = getUserFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserFeedOutputModel(listOf(recipeInfo2, recipeInfo)), response.body)
    }

    @Test
    fun `Should retrieve user's feed according to intolerances`() {
        // given a user (testAuthenticatedUser) with intolerances that follows another user, pagination params (pagingParams) and a recipe
        val testAuthenticatedUserWithIntolerances = testAuthenticatedUser.copy(
            user = testAuthenticatedUser.user.copy(intolerances = listOf(Intolerance.GLUTEN))
        )

        // mock
        whenever(
            feedServiceMock.getFeed(
                testAuthenticatedUserWithIntolerances.user.id,
                listOf(Intolerance.GLUTEN),
                emptyList(),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo2))

        // when retrieving the feed with intolerance and no diets
        val response = getUserFeed(testAuthenticatedUserWithIntolerances, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserFeedOutputModel(listOf(recipeInfo2)), response.body)
    }

    @Test
    fun `Should retrieve user's feed according to diets`() {
        // given a user (testAuthenticatedUser) with diets that follows another user, pagination params (pagingParams) and a recipe
        val testAuthenticatedUserWithDiets = testAuthenticatedUser.copy(
            user = testAuthenticatedUser.user.copy(diets = listOf(Diet.LACTO_VEGETARIAN))
        )

        // mock
        whenever(
            feedServiceMock.getFeed(
                testAuthenticatedUserWithDiets.user.id,
                emptyList(),
                listOf(Diet.LACTO_VEGETARIAN),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo))
        // when retrieving the feed with diets and no intolerances
        val response = getUserFeed(testAuthenticatedUserWithDiets, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserFeedOutputModel(listOf(recipeInfo)), response.body)
    }

    @Test
    fun `Should retrieve user's feed according to intolerances and diets`() {
        // given a user (testAuthenticatedUser) with intolerances and diets that follows another user, pagination params (pagingParams) and a recipe
        val testAuthenticatedUserWithIntolerancesAndDiets = testAuthenticatedUser.copy(
            user = testAuthenticatedUser.user.copy(
                intolerances = listOf(Intolerance.GLUTEN),
                diets = listOf(Diet.LACTO_VEGETARIAN)
            )
        )

        // mock
        whenever(
            feedServiceMock.getFeed(
                testAuthenticatedUserWithIntolerancesAndDiets.user.id,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.LACTO_VEGETARIAN),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo2, recipeInfo))

        // when retrieving the feed with intolerance and no diets
        val response = getUserFeed(testAuthenticatedUserWithIntolerancesAndDiets, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GetUserFeedOutputModel(listOf(recipeInfo2, recipeInfo)), response.body)
    }
}
