package epicurius.unit.http.feed

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.http.feed.models.output.FeedOutputModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.Test

class GetFeedHttpTests : FeedHttpTest() {
    @Test
    fun `Should retrieve user's empty feed`() {
        // given a user that follows no users and pagination params
        val testAuthenticatedUser = createAuthenticatedUser()
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, emptyList(), emptyList(), pagingParams)
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
        val testAuthenticatedUser = createAuthenticatedUser()
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, emptyList(), emptyList(), pagingParams)
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
        val testAuthenticatedUser = createAuthenticatedUser()
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(testAuthenticatedUser.user.id, emptyList(), emptyList(), pagingParams)
        ).thenReturn(listOf(recipeInfo2, recipeInfo))

        // when retrieving the feed
        val response = getFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FeedOutputModel(listOf(recipeInfo2, recipeInfo)), response.body)
    }

    @Test
    fun `Should retrieve user's feed according to intolerances`() {
        // given a user with intolerances that follows another user, pagination params and a recipe
        val testAuthenticatedUser = createAuthenticatedUser(intolerances = listOf(Intolerance.GLUTEN))
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(
                testAuthenticatedUser.user.id,
                listOf(Intolerance.GLUTEN),
                emptyList(),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo2))

        // when retrieving the feed with intolerance and no diets
        val response = getFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FeedOutputModel(listOf(recipeInfo2)), response.body)
    }

    @Test
    fun `Should retrieve user's feed according to diets`() {
        // given a user with diets that follows another user, pagination params and a recipe
        val testAuthenticatedUser = createAuthenticatedUser(diets = listOf(Diet.LACTO_VEGETARIAN))
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(
                testAuthenticatedUser.user.id,
                emptyList(),
                listOf(Diet.LACTO_VEGETARIAN),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo))

        // when retrieving the feed with diets and no intolerances
        val response = getFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FeedOutputModel(listOf(recipeInfo)), response.body)
    }

    @Test
    fun `Should retrieve user's feed according to intolerances and diets`() {
        // given a user with intolerances and diets that follows another user, pagination params and a recipe
        val testAuthenticatedUser = createAuthenticatedUser(
            intolerances = listOf(Intolerance.GLUTEN), diets = listOf(Diet.LACTO_VEGETARIAN)
        )
        val pagingParams = PagingParams(0, 10)

        // mock
        whenever(
            feedServiceMock.getFeed(
                testAuthenticatedUser.user.id,
                listOf(Intolerance.GLUTEN),
                listOf(Diet.LACTO_VEGETARIAN),
                pagingParams
            )
        ).thenReturn(listOf(recipeInfo2, recipeInfo))

        // when retrieving the feed with intolerance and no diets
        val response = getFeed(testAuthenticatedUser, pagingParams.skip, pagingParams.limit)

        // then feed should contain recipes
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(FeedOutputModel(listOf(recipeInfo2, recipeInfo)), response.body)
    }
}
