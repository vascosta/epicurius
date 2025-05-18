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
        ) = userController.getUserInfo(authenticatedUser)

        fun getUserProfile(
            authenticatedUser: AuthenticatedUser,
            name: String,
        ) =
            userController.getUserProfile(authenticatedUser, name)

        fun searchUsers(
            authenticatedUser: AuthenticatedUser,
            partialUsername: String,
            pagingParams: PagingParams,
        ) = userController.searchUsers(authenticatedUser, partialUsername, pagingParams.skip, pagingParams.limit)

        fun getUserIntolerances(
            authenticatedUser: AuthenticatedUser,
        ) = userController.getUserIntolerances(authenticatedUser)

        fun getUserDiet(
            authenticatedUser: AuthenticatedUser,
        ) =
            userController.getUserDiet(authenticatedUser)

        fun getUserFollowers(
            authenticatedUser: AuthenticatedUser,
            skip: Int,
            limit: Int
        ) = userController.getUserFollowers(authenticatedUser, skip, limit)

        fun getUserFollowing(
            authenticatedUser: AuthenticatedUser,
            skip: Int,
            limit: Int
        ) = userController.getUserFollowing(authenticatedUser, skip, limit)

        fun getUserFollowRequests(
            authenticatedUser: AuthenticatedUser,
        ) = userController.getUserFollowRequests(authenticatedUser)

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
        ) =
            userController.updateUser(authenticatedUser, body)

        fun updateUserProfilePicture(
            authenticatedUser: AuthenticatedUser,
            profilePicture: MultipartFile?,
        ) =
            userController.updateUserProfilePicture(authenticatedUser, profilePicture)

        fun resetUserPassword(body: ResetPasswordInputModel) = userController.resetUserPassword(body)

        fun follow(
            authenticatedUser: AuthenticatedUser,
            username: String,
        ) = userController.follow(authenticatedUser, username)

        fun cancelFollowRequest(
            authenticatedUser: AuthenticatedUser,
            username: String,
        ) =
            userController.followRequest(authenticatedUser, username, FollowRequestType.CANCEL)

        fun unfollow(
            authenticatedUser: AuthenticatedUser,
            username: String,
        ) = userController.unfollow(authenticatedUser, username)

        fun deleteUser(
            authenticatedUser: AuthenticatedUser,
            response: HttpServletResponse
        ) = userController.deleteUser(authenticatedUser, response)

        // FEED
        fun getUserFeed(
            authenticatedUser: AuthenticatedUser,
            skip: Int,
            limit: Int,
        ) =
            feedController.getUserFeed(authenticatedUser, skip, limit)

        // FRIDGE
        fun getFridge(
            authenticatedUser: AuthenticatedUser,
        ) = fridgeController.getFridge(authenticatedUser)

        suspend fun addProducts(
            authenticatedUser: AuthenticatedUser,
            product: ProductInputModel,
        ) =
            fridgeController.addProducts(authenticatedUser, product)

        fun updateProduct(
            authenticatedUser: AuthenticatedUser,
            entryNumber: Int,
            updateProductInputModel: UpdateProductInputModel,
        ) = fridgeController.updateFridgeProduct(authenticatedUser, entryNumber, updateProductInputModel)

        fun removeFridgeProduct(
            authenticatedUser: AuthenticatedUser,
            entryNumber: Int,
        ) =
            fridgeController.removeFridgeProduct(authenticatedUser, entryNumber)

        // RECIPE
        suspend fun getRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int,
        ) =
            recipeController.getRecipe(authenticatedUser, id)

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
            limit = 5
        )

        suspend fun createRecipe(
            authenticatedUser: AuthenticatedUser,
            createRecipeInputModel: String,
            pictures: List<MultipartFile>
        ) = recipeController.createRecipe(authenticatedUser, createRecipeInputModel, pictures)

        suspend fun updateRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            updateRecipeInputModel: UpdateRecipeInputModel
        ) =
            recipeController.updateRecipe(authenticatedUser, id, updateRecipeInputModel)

        suspend fun updateRecipePictures(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            pictures: List<MultipartFile>
        ) =
            recipeController.updateRecipePictures(authenticatedUser, id, pictures)

        fun deleteRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int
        ) =
            recipeController.deleteRecipe(authenticatedUser, id)

        // RATE RECIPE
        fun getRecipeRate(
            authenticatedUser: AuthenticatedUser,
            id: Int
        ) =
            rateRecipeController.getRecipeRate(authenticatedUser, id)

        fun getUserRecipeRate(
            authenticatedUser: AuthenticatedUser,
            id: Int
        ) =
            rateRecipeController.getUserRecipeRate(authenticatedUser, id)

        fun rateRecipe(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            rating: Int
        ) =
            rateRecipeController.rateRecipe(authenticatedUser, id, RateRecipeInputModel(rating))

        fun updateRecipeRate(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            rating: Int
        ) =
            rateRecipeController.updateRecipeRate(authenticatedUser, id, RateRecipeInputModel(rating))

        fun deleteRecipeRate(
            authenticatedUser: AuthenticatedUser,
            id: Int
        ) =
            rateRecipeController.deleteRecipeRate(authenticatedUser, id)

        // INGREDIENTS
        suspend fun getIngredients(
            authenticatedUser: AuthenticatedUser,
            partial: String
        ) =
            ingredientsController.getIngredients(authenticatedUser, partial)

        suspend fun getSubstituteIngredients(
            authenticatedUser: AuthenticatedUser,
            name: String
        ) =
            ingredientsController.getSubstituteIngredients(authenticatedUser, name)

        suspend fun getIngredientsFromPicture(
            authenticatedUser: AuthenticatedUser,
            picture: MultipartFile
        ) =
            ingredientsController.getIngredientsFromPicture(authenticatedUser, picture)

        // MENU
        fun getDailyMenu(
            authenticatedUser: AuthenticatedUser
        ) =
            menuController.getDailyMenu(authenticatedUser)

        // COLLECTION
        fun getCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int
        ) =
            collectionController.getCollection(authenticatedUser, id)

        fun createCollection(
            authenticatedUser: AuthenticatedUser,
            body: CreateCollectionInputModel
        ) =
            collectionController.createCollection(authenticatedUser, body)

        fun addRecipeToCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            body: AddRecipeToCollectionInputModel
        ) = collectionController.addRecipeToCollection(authenticatedUser, id, body)

        fun updateCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            body: UpdateCollectionInputModel,
        ) = collectionController.updateCollection(authenticatedUser, id, body)

        fun removeRecipeFromCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int,
            recipeId: Int,
        ) = collectionController.removeRecipeFromCollection(authenticatedUser, id, recipeId)

        fun deleteCollection(
            authenticatedUser: AuthenticatedUser,
            id: Int
        ) =
            collectionController.deleteCollection(authenticatedUser, id)

        // MEAL PLANNER
        fun getWeeklyMealPlanner(
            authenticatedUser: AuthenticatedUser
        ) = mealPlannerController.getWeeklyMealPlanner(authenticatedUser)

        fun getDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
        ) = mealPlannerController.getDailyMealPlanner(authenticatedUser, date)

        fun createDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            body: CreateMealPlannerInputModel
        ) = mealPlannerController.createDailyMealPlanner(authenticatedUser, body)

        fun addRecipeToDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            body: AddMealPlannerInputModel,
        ) = mealPlannerController.addRecipeToDailyMealPlanner(authenticatedUser, date, body)

        fun updateDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            body: UpdateMealPlannerInputModel,
        ) = mealPlannerController.updateDailyMealPlanner(authenticatedUser, date, body)

        fun updateDailyCalories(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            body: UpdateDailyCaloriesInputModel,
        ) = mealPlannerController.updateDailyCalories(authenticatedUser, date, body)

        fun removeMealTimeFromDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate,
            mealTime: MealTime
        ) = mealPlannerController.removeMealTimeFromDailyMealPlanner(authenticatedUser, date, mealTime)

        fun deleteDailyMealPlanner(
            authenticatedUser: AuthenticatedUser,
            date: LocalDate
        ) = mealPlannerController.deleteDailyMealPlanner(authenticatedUser, date)
    }
}
