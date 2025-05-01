package epicurius.unit.services.feed

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.services.feed.models.GetFeedModel
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetFeedServiceTest : FeedServiceTest() {

    @Test
    fun `Should retrieve user's empty feed`() {
        // given a user that follows no users and pagination params
        val pagingParams = PagingParams(0, 10)
        val info = GetFeedModel(USER_ID, emptyList(), emptyList(), pagingParams)

        // mock
        whenever(jdbiFeedRepositoryMock.getFeed(info)).thenReturn(emptyList())
        whenever(pictureRepositoryMock.getPicture("", RECIPES_FOLDER)).thenReturn(null)

        // when retrieving the feed
        val feed = getFeed(info.userId, info.intolerances, info.diets, info.pagingParams)

        // then feed should be empty
        assert(feed.isEmpty())
    }

    @Test
    fun `Should retrieve user's feed with recipes`() {
        // given a user that follows another user with recipe and pagination params
        val pagingParams = PagingParams(0, 10)
        val info = GetFeedModel(USER_ID, emptyList(), emptyList(), pagingParams)

        // mock
        whenever(jdbiFeedRepositoryMock.getFeed(info)).thenReturn(listOf(jdbiRecipeInfo))
        whenever(
            pictureRepositoryMock.getPicture(jdbiRecipeInfo.pictures.first(), RECIPES_FOLDER)
        ).thenReturn(ByteArray(0))

        // when retrieving the feed
        val feed = getFeed(info.userId, info.intolerances, info.diets, info.pagingParams)

        // then feed should contain recipes
        assertTrue(feed.isNotEmpty())
        assertEquals(jdbiRecipeInfo.id, feed.first().id)
        assertEquals(jdbiRecipeInfo.name, feed.first().name)
        assertEquals(jdbiRecipeInfo.cuisine, feed.first().cuisine)
        assertEquals(jdbiRecipeInfo.mealType, feed.first().mealType)
        assertEquals(jdbiRecipeInfo.preparationTime, feed.first().preparationTime)
        assertEquals(jdbiRecipeInfo.servings, feed.first().servings)
    }

    @Test
    fun `Should retrieve user's feed order by most recent recipe`() {
        // given a user that follows another user with recipe and pagination params
        val pagingParams = PagingParams(0, 10)
        val info = GetFeedModel(USER_ID, emptyList(), emptyList(), pagingParams)

        // mock
        whenever(jdbiFeedRepositoryMock.getFeed(info)).thenReturn(listOf(jdbiRecipeInfo2, jdbiRecipeInfo))
        whenever(
            pictureRepositoryMock.getPicture(jdbiRecipeInfo.pictures.first(), RECIPES_FOLDER)
        ).thenReturn(ByteArray(0))
        whenever(
            pictureRepositoryMock.getPicture(jdbiRecipeInfo2.pictures.first(), RECIPES_FOLDER)
        ).thenReturn(ByteArray(0))

        // when retrieving the feed
        val feed = getFeed(info.userId, info.intolerances, info.diets, info.pagingParams)

        // then feed should contain recipes
        assertTrue(feed.isNotEmpty())
        assertEquals(2, feed.size)
        assertEquals(recipeInfo2.id, feed.first().id)
        assertEquals(recipeInfo2.name, feed.first().name)
        assertEquals(recipeInfo2.cuisine, feed.first().cuisine)
        assertEquals(recipeInfo2.mealType, feed.first().mealType)
        assertEquals(recipeInfo2.preparationTime, feed.first().preparationTime)
        assertEquals(recipeInfo2.servings, feed.first().servings)
        assertEquals(recipeInfo.id, feed.last().id)
        assertEquals(recipeInfo.name, feed.last().name)
        assertEquals(recipeInfo.cuisine, feed.last().cuisine)
        assertEquals(recipeInfo.mealType, feed.last().mealType)
        assertEquals(recipeInfo.preparationTime, feed.last().preparationTime)
        assertEquals(recipeInfo.servings, feed.last().servings)
    }

    @Test
    fun `Should retrieve user's feed according to intolerances`() {
        // given a user with intolerances and diets that follows another user with recipe, pagination params and a recipe
        val pagingParams = PagingParams(0, 10)
        val info = GetFeedModel(USER_ID, listOf(Intolerance.GLUTEN), emptyList(), pagingParams)

        // mock
        whenever(jdbiFeedRepositoryMock.getFeed(info)).thenReturn(emptyList())
        whenever(
            pictureRepositoryMock.getPicture(jdbiRecipeInfo.pictures.first(), RECIPES_FOLDER)
        ).thenReturn(ByteArray(0))

        // when retrieving the feed
        val feed = getFeed(info.userId, info.intolerances, info.diets, info.pagingParams)

        // then feed should be empty
        assertTrue(feed.isEmpty())
    }

    @Test
    fun `Should retrieve user's feed according to diets`() {
        // given a user with intolerances and diets that follows another user with recipe, pagination params and a recipe
        val pagingParams = PagingParams(0, 10)
        val info = GetFeedModel(USER_ID, emptyList(), listOf(Diet.LACTO_VEGETARIAN), pagingParams)

        // mock
        whenever(jdbiFeedRepositoryMock.getFeed(info)).thenReturn(listOf(jdbiRecipeInfo))
        whenever(
            pictureRepositoryMock.getPicture(jdbiRecipeInfo.pictures.first(), RECIPES_FOLDER)
        ).thenReturn(ByteArray(0))

        // when retrieving the feed
        val feed = getFeed(info.userId, info.intolerances, info.diets, info.pagingParams)

        // then feed should contain recipes
        assertTrue(feed.isNotEmpty())
        assertEquals(jdbiRecipeInfo.id, feed.first().id)
        assertEquals(jdbiRecipeInfo.name, feed.first().name)
        assertEquals(jdbiRecipeInfo.cuisine, feed.first().cuisine)
        assertEquals(jdbiRecipeInfo.mealType, feed.first().mealType)
        assertEquals(jdbiRecipeInfo.preparationTime, feed.first().preparationTime)
        assertEquals(jdbiRecipeInfo.servings, feed.first().servings)
    }

    @Test
    fun `Should retrieve user's feed according to intolerances and diets`() {
        // given a user with intolerances and diets that follows another user with recipe, pagination params and a recipe
        val pagingParams = PagingParams(0, 10)
        val info = GetFeedModel(USER_ID, listOf(Intolerance.GLUTEN), listOf(Diet.LACTO_VEGETARIAN), pagingParams)

        // mock
        whenever(jdbiFeedRepositoryMock.getFeed(info)).thenReturn(emptyList())
        whenever(
            pictureRepositoryMock.getPicture(jdbiRecipeInfo.pictures.first(), RECIPES_FOLDER)
        ).thenReturn(ByteArray(0))

        // when retrieving the feed
        val feed = getFeed(info.userId, info.intolerances, info.diets, info.pagingParams)

        // then feed should be empty
        assertTrue(feed.isEmpty())
    }
}
