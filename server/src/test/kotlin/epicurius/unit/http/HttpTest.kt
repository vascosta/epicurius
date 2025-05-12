package epicurius.unit.http

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.FollowRequestType
import epicurius.http.controllers.collection.models.input.AddRecipeToCollectionInputModel
import epicurius.http.controllers.collection.models.input.CreateCollectionInputModel
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import epicurius.http.controllers.fridge.models.input.ProductInputModel
import epicurius.http.controllers.fridge.models.input.UpdateProductInputModel
import epicurius.http.controllers.mealPlanner.models.input.AddMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.input.CreateMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateDailyCaloriesInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateMealPlannerInputModel
import epicurius.http.controllers.rateRecipe.models.input.RateRecipeInputModel
import epicurius.http.controllers.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.controllers.user.models.input.LoginInputModel
import epicurius.http.controllers.user.models.input.ResetPasswordInputModel
import epicurius.http.controllers.user.models.input.SignUpInputModel
import epicurius.http.controllers.user.models.input.UpdateUserInputModel
import epicurius.unit.EpicuriusUnitTest
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.kotlin.reset
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

open class HttpTest : EpicuriusUnitTest() {

    @BeforeEach
    fun resetMockResponse() {
        reset(mockResponse)
    }

    companion object {
        val mockResponse: HttpServletResponse = mock()
        val mockCookie: Cookie = mock()

        // USER
        fun getUserInfo(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = userController.getUserInfo(authenticatedUser, response)

        fun getUserProfile(
            authenticatedUser: AuthenticatedUser,
            name: String,
            response: HttpServletResponse
        ) =
            userController.getUserProfile(authenticatedUser, name, response)

        fun searchUsers(
            authenticatedUser: AuthenticatedUser,
            partialUsername: String,
            pagingParams: PagingParams,
            response: HttpServletResponse
        ) = userController.searchUsers(authenticatedUser, partialUsername, pagingParams.skip, pagingParams.limit, response)

        fun getUserIntolerances(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = userController.getUserIntolerances(authenticatedUser, response)

        fun getUserDiet(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) =
            userController.getUserDiet(authenticatedUser, response)

        fun getUserFollowers(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = userController.getUserFollowers(authenticatedUser, response)

        fun getUserFollowing(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = userController.getUserFollowing(authenticatedUser, response)

        fun getUserFollowRequests(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = userController.getUserFollowRequests(authenticatedUser, response)

        fun signUp(
            body: SignUpInputModel,
            response: HttpServletResponse
        ) = userController.signUp(body, response)

        fun login(
            body: LoginInputModel,
            response: HttpServletResponse
        ) = userController.login(body, response)

        fun logout(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = userController.logout(authenticatedUser, response)

        fun updateUser(
            authenticatedUser: AuthenticatedUser,
            body: UpdateUserInputModel,
            response: HttpServletResponse
        ) =
            userController.updateUser(authenticatedUser, body, response)

        fun updateUserProfilePicture(
            authenticatedUser: AuthenticatedUser,
            profilePicture: MultipartFile?,
            response: HttpServletResponse
        ) =
            userController.updateUserProfilePicture(authenticatedUser, profilePicture, response)

        fun resetUserPassword(body: ResetPasswordInputModel) = userController.resetUserPassword(body)

        fun follow(
            authenticatedUser: AuthenticatedUser,
            username: String,
            response: HttpServletResponse
        ) = userController.follow(authenticatedUser, username, response)

        fun cancelFollowRequest(
            authenticatedUser: AuthenticatedUser,
            username: String,
            response: HttpServletResponse
        ) =
            userController.followRequest(authenticatedUser, username, FollowRequestType.CANCEL, response)

        fun unfollow(
            authenticatedUser: AuthenticatedUser,
            username: String,
            response: HttpServletResponse
        ) = userController.unfollow(authenticatedUser, username, response)

        // FEED
        fun getFeed(
            authenticatedUser: AuthenticatedUser,
            skip: Int,
            limit: Int,
            response: HttpServletResponse
        ) =
            feedController.getFeed(authenticatedUser, skip, limit, response)

        // FRIDGE
        fun getFridge(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = fridgeController.getFridge(authenticatedUser, response)

        suspend fun addProducts(
            authenticatedUser: AuthenticatedUser,
            product: ProductInputModel,
            response: HttpServletResponse
        ) =
            fridgeController.addProducts(authenticatedUser, product, response)

        fun updateProduct(
            authenticatedUser: AuthenticatedUser,
            entryNumber: Int,
            updateProductInputModel: UpdateProductInputModel,
            response: HttpServletResponse
        ) = fridgeController.updateFridgeProduct(authenticatedUser, entryNumber, updateProductInputModel, response)

        fun removeFridgeProduct(
            authenticatedUser: AuthenticatedUser,
            entryNumber: Int,
            response: HttpServletResponse
        ) =
            fridgeController.removeFridgeProduct(authenticatedUser, entryNumber, response)

        // RECIPE
        suspend fun getRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            response: HttpServletResponse
        ) =
            recipeController.getRecipe(authenticatedUser, id, response)

        fun searchRecipes(
            authenticatedUser: AuthenticatedUser,
            name: String? = null,
            cuisine: List<Cuisine>? = null,
            mealType: List<MealType>? = null,
            ingredients: List<String>? = null,
            intolerances: List<Intolerance>? = null,
            diets: List<Diet>? = null,
            minCalories: Int? = null,
            maxCalories: Int? = null,
            minCarbs: Int? = null,
            maxCarbs: Int? = null,
            minFat: Int? = null,
            maxFat: Int? = null,
            minProtein: Int? = null,
            maxProtein: Int? = null,
            minTime: Int? = null,
            maxTime: Int? = null,
            response: HttpServletResponse
        ) = recipeController.searchRecipes(
            authenticatedUser,
            name,
            cuisine,
            mealType,
            ingredients,
            intolerances,
            diets,
            minCalories,
            maxCalories,
            minCarbs,
            maxCarbs,
            minFat,
            maxFat,
            minProtein,
            maxProtein,
            minTime,
            maxTime,
            skip = 0,
            limit = 5,
            response
        )

        suspend fun createRecipe(
            authenticatedUser: AuthenticatedUser,
            createRecipeInputModel: String,
            pictures: List<MultipartFile>,
            response: HttpServletResponse
        ) = recipeController.createRecipe(authenticatedUser, createRecipeInputModel, pictures, response)

        suspend fun updateRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            updateRecipeInputModel: UpdateRecipeInputModel,
            response: HttpServletResponse
        ) =
            recipeController.updateRecipe(authenticatedUser, id, updateRecipeInputModel, response)

        suspend fun updateRecipePictures(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            pictures: List<MultipartFile>,
            response: HttpServletResponse
        ) =
            recipeController.updateRecipePictures(authenticatedUser, id, pictures, response)

        fun deleteRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            response: HttpServletResponse
        ) =
            recipeController.deleteRecipe(authenticatedUser, id, response)

        // RATE RECIPE
        fun getRecipeRate(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            response: HttpServletResponse
        ) =
            rateRecipeController.getRecipeRate(authenticatedUser, id, response)

        fun rateRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            rating: Int,
            response: HttpServletResponse
        ) =
            rateRecipeController.rateRecipe(authenticatedUser, id, RateRecipeInputModel(rating), response)

        fun updateRecipeRate(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            rating: Int,
            response: HttpServletResponse
        ) =
            rateRecipeController.updateRecipeRate(authenticatedUser, id, RateRecipeInputModel(rating), response)

        fun deleteRecipeRate(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            response: HttpServletResponse
        ) =
            rateRecipeController.deleteRecipeRate(authenticatedUser, id, response)

        // INGREDIENTS
        suspend fun getIngredients(
            authenticatedUser: AuthenticatedUser,
            partial: String,
            response: HttpServletResponse
        ) =
            ingredientsController.getIngredients(authenticatedUser, partial, response)

        suspend fun getSubstituteIngredients(
            authenticatedUser: AuthenticatedUser,
            name: String,
            response: HttpServletResponse
        ) =
            ingredientsController.getSubstituteIngredients(authenticatedUser, name, response)

        suspend fun getIngredientsFromPicture(
            authenticatedUser: AuthenticatedUser,
            picture: MultipartFile,
            response: HttpServletResponse
        ) =
            ingredientsController.getIngredientsFromPicture(authenticatedUser, picture, response)

        // MENU
        fun getDailyMenu(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) =
            menuController.getDailyMenu(authenticatedUser, response)

        // COLLECTION
        fun getCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            response: HttpServletResponse
        ) =
            collectionController.getCollection(authenticatedUser, id, response)

        fun createCollection(
            authenticatedUser: AuthenticatedUser,
            body: CreateCollectionInputModel,
            response: HttpServletResponse
        ) =
            collectionController.createCollection(authenticatedUser, body, response)

        fun addRecipeToCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            body: AddRecipeToCollectionInputModel,
            response: HttpServletResponse
        ) = collectionController.addRecipeToCollection(authenticatedUser, id, body, response)

        fun updateCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            body: UpdateCollectionInputModel,
            response: HttpServletResponse
        ) = collectionController.updateCollection(authenticatedUser, id, body, response)

        fun removeRecipeFromCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            recipeId: Int,
            response: HttpServletResponse
        ) = collectionController.removeRecipeFromCollection(authenticatedUser, id, recipeId, response)

        fun deleteCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            response: HttpServletResponse
        ) =
            collectionController.deleteCollection(authenticatedUser, id, response)

        // MEAL PLANNER
        fun getWeeklyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = mealPlannerController.getWeeklyMealPlanner(authenticatedUser, response)

        fun getDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            response: HttpServletResponse
        ) = mealPlannerController.getDailyMealPlanner(authenticatedUser, date, response)

        fun createDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            body: CreateMealPlannerInputModel,
            response: HttpServletResponse
        ) = mealPlannerController.createDailyMealPlanner(authenticatedUser, body, response)

        fun addRecipeToDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            body: AddMealPlannerInputModel,
            response: HttpServletResponse
        ) = mealPlannerController.addRecipeToDailyMealPlanner(authenticatedUser, date, body, response)

        fun updateDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            body: UpdateMealPlannerInputModel,
            response: HttpServletResponse
        ) = mealPlannerController.updateDailyMealPlanner(authenticatedUser, date, body, response)

        fun updateDailyCalories(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            body: UpdateDailyCaloriesInputModel,
            response: HttpServletResponse
        ) = mealPlannerController.updateDailyCalories(authenticatedUser, date, body, response)

        fun removeMealTimeFromDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            mealTime: MealTime,
            response: HttpServletResponse
        ) = mealPlannerController.removeMealTimeFromDailyMealPlanner(authenticatedUser, date, mealTime, response)

        fun deleteDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            response: HttpServletResponse
        ) = mealPlannerController.deleteDailyMealPlanner(authenticatedUser, date, response)
    }
}
