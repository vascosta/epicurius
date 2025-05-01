package epicurius.unit.http

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.FollowRequestType
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.user.models.input.LoginInputModel
import epicurius.http.user.models.input.ResetPasswordInputModel
import epicurius.http.user.models.input.SignUpInputModel
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.unit.EpicuriusUnitTest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.multipart.MultipartFile

open class HttpTest : EpicuriusUnitTest() {

    companion object {
        // USER
        fun getUserInfo(authenticatedUser: AuthenticatedUser) = userController.getUserInfo(authenticatedUser)

        fun getUserProfile(authenticatedUser: AuthenticatedUser, name: String) =
            userController.getUserProfile(authenticatedUser, name)

        fun searchUsers(
            authenticatedUser: AuthenticatedUser,
            partialUsername: String,
            pagingParams: PagingParams
        ) = userController.searchUsers(authenticatedUser, partialUsername, pagingParams.skip, pagingParams.limit)

        fun getUserIntolerances(authenticatedUser: AuthenticatedUser) = userController.getUserIntolerances(authenticatedUser)
        fun getUserDiet(authenticatedUser: AuthenticatedUser) = userController.getUserDiet(authenticatedUser)

        fun getUserFollowers(authenticatedUser: AuthenticatedUser) = userController.getUserFollowers(authenticatedUser)
        fun getUserFollowing(authenticatedUser: AuthenticatedUser) = userController.getUserFollowing(authenticatedUser)
        fun getUserFollowRequests(authenticatedUser: AuthenticatedUser) = userController.getUserFollowRequests(authenticatedUser)

        fun signUp(body: SignUpInputModel, response: HttpServletResponse) = userController.signUp(body, response)
        fun login(body: LoginInputModel, response: HttpServletResponse) = userController.login(body, response)
        fun logout(authenticatedUser: AuthenticatedUser, response: HttpServletResponse) = userController.logout(authenticatedUser, response)

        fun updateUser(authenticatedUser: AuthenticatedUser, body: UpdateUserInputModel) =
            userController.updateUser(authenticatedUser, body)

        fun updateUserProfilePicture(authenticatedUser: AuthenticatedUser, profilePicture: MultipartFile?) =
            userController.updateUserProfilePicture(authenticatedUser, profilePicture)

        fun resetUserPassword(body: ResetPasswordInputModel) = userController.resetUserPassword(body)

        fun follow(authenticatedUser: AuthenticatedUser, username: String) = userController.follow(authenticatedUser, username)

        fun cancelFollowRequest(authenticatedUser: AuthenticatedUser, username: String) =
            userController.followRequest(authenticatedUser, username, FollowRequestType.CANCEL)

        fun unfollow(authenticatedUser: AuthenticatedUser, username: String) = userController.unfollow(authenticatedUser, username)

        // FEED
        fun getFeed(authenticatedUser: AuthenticatedUser, skip: Int, limit: Int) =
            feedController.getFeed(authenticatedUser, skip, limit)

        // FRIDGE
        fun getFridge(authenticatedUser: AuthenticatedUser) = fridgeController.getFridge(authenticatedUser)

        suspend fun addProducts(authenticatedUser: AuthenticatedUser, product: ProductInputModel) =
            fridgeController.addProducts(authenticatedUser, product)

        fun updateProduct(
            authenticatedUser: AuthenticatedUser,
            entryNumber: Int,
            updateProductInputModel: UpdateProductInputModel
        ) = fridgeController.updateFridgeProduct(authenticatedUser, entryNumber, updateProductInputModel)

        fun removeFridgeProduct(authenticatedUser: AuthenticatedUser, entryNumber: Int) =
            fridgeController.removeFridgeProduct(authenticatedUser, entryNumber)

        // RECIPE
        suspend fun getRecipe(authenticatedUser: AuthenticatedUser, id: Int) =
            recipeController.getRecipe(authenticatedUser, id)

        fun searchRecipes(
            authenticatedUser: AuthenticatedUser,
            name: String?,
            cuisine: Cuisine?,
            mealType: MealType?,
            ingredients: List<String>?,
            intolerances: List<Intolerance>?,
            diets: List<Diet>?,
            minCalories: Int?,
            maxCalories: Int?,
            minCarbs: Int?,
            maxCarbs: Int?,
            minFat: Int?,
            maxFat: Int?,
            minProtein: Int?,
            maxProtein: Int?,
            minTime: Int?,
            maxTime: Int?
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
            maxTime
        )

        suspend fun createRecipe(
            authenticatedUser: AuthenticatedUser,
            createRecipeInputModel: String,
            pictures: List<MultipartFile>
        ) = recipeController.createRecipe(authenticatedUser, createRecipeInputModel, pictures)

        suspend fun updateRecipe(authenticatedUser: AuthenticatedUser, id: Int, updateRecipeInputModel: UpdateRecipeInputModel) =
            recipeController.updateRecipe(authenticatedUser, id, updateRecipeInputModel)

        suspend fun updateRecipePictures(authenticatedUser: AuthenticatedUser, id: Int, pictures: List<MultipartFile>) =
            recipeController.updateRecipePictures(authenticatedUser, id, pictures)

        fun deleteRecipe(authenticatedUser: AuthenticatedUser, id: Int) =
            recipeController.deleteRecipe(authenticatedUser, id)

        // MENU
        fun getDailyMenu(authenticatedUser: AuthenticatedUser) =
            menuController.getDailyMenu(authenticatedUser)
    }
}
