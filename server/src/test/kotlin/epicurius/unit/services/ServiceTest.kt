package epicurius.unit.services

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.user.FollowRequestType
import epicurius.http.fridge.models.input.ProductInputModel
import epicurius.http.fridge.models.input.UpdateProductInputModel
import epicurius.http.recipe.models.input.CreateRecipeInputModel
import epicurius.http.recipe.models.input.SearchRecipesInputModel
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.user.models.input.UpdateUserInputModel
import epicurius.unit.EpicuriusUnitTest
import org.springframework.web.multipart.MultipartFile

open class ServiceTest : EpicuriusUnitTest() {

    companion object {
        // USER
        fun createUser(username: String, email: String, country: String, password: String, confirmPassword: String) =
            userService.createUser(username, email, country, password, confirmPassword)

        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)

        fun getUserProfile(username: String) = userService.getUserProfile(username)

        fun getProfilePicture(profilePictureName: String?) = userService.getProfilePicture(profilePictureName)

        fun searchUsers(userId: Int, partialUsername: String, pagingParams: PagingParams) =
            userService.searchUsers(userId, partialUsername, pagingParams)

        fun getFollowers(userId: Int) = userService.getFollowers(userId)

        fun getFollowing(userId: Int) = userService.getFollowing(userId)

        fun getFollowRequests(userId: Int) = userService.getFollowRequests(userId)

        fun login(username: String? = null, email: String? = null, password: String) =
            userService.login(username, email, password)

        fun logout(username: String) = userService.logout(username)

        fun updateUser(username: String, userUpdate: UpdateUserInputModel) =
            userService.updateUser(username, userUpdate)

        fun updateProfilePicture(username: String, profilePictureName: String? = null, profilePicture: MultipartFile? = null) =
            userService.updateProfilePicture(username, profilePictureName, profilePicture)

        fun resetPassword(email: String, newPassword: String, confirmPassword: String) =
            userService.resetPassword(email, newPassword, confirmPassword)

        fun follow(userId: Int, username: String, usernameToFollow: String) = userService.follow(userId, username, usernameToFollow)

        fun unfollow(userId: Int, username: String, usernameToUnfollow: String) = userService.unfollow(userId, username, usernameToUnfollow)

        fun cancelFollowRequest(userId: Int, username: String, usernameToRequest: String) =
            userService.followRequest(userId, username, usernameToRequest, FollowRequestType.CANCEL)

        // FEED
        fun getFeed(
            userId: Int,
            intolerances: List<Intolerance>,
            diets: List<Diet>,
            pagingParams: PagingParams
        ) = feedService.getFeed(userId, intolerances, diets, pagingParams)

        // FRIDGE
        fun getFridge(userId: Int) = fridgeService.getFridge(userId)

        suspend fun addProduct(userId: Int, productName: ProductInputModel) =
            fridgeService.addProduct(userId, productName)

        fun updateProductInfo(userId: Int, entryNumber: Int, product: UpdateProductInputModel) =
            fridgeService.updateProductInfo(userId, entryNumber, product)

        fun removeProduct(userId: Int, entryNumber: Int) = fridgeService.removeProduct(userId, entryNumber)

        // RECIPE
        suspend fun createRecipe(authorId: Int, authorName: String, recipeInfo: CreateRecipeInputModel, pictures: Set<MultipartFile>) =
            recipeService.createRecipe(authorId, authorName, recipeInfo, pictures)

        suspend fun getRecipe(recipeId: Int, username: String) = recipeService.getRecipe(recipeId, username)

        fun searchRecipes(userId: Int, form: SearchRecipesInputModel, skip: Int, limit: Int) =
            recipeService.searchRecipes(userId, form, PagingParams(skip, limit))

        suspend fun updateRecipe(userId: Int, recipeId: Int, recipeInfo: UpdateRecipeInputModel) =
            recipeService.updateRecipe(userId, recipeId, recipeInfo)

        fun updateRecipePictures(userId: Int, recipeId: Int, pictures: Set<MultipartFile>) =
            recipeService.updateRecipePictures(userId, recipeId, pictures)

        fun deleteRecipe(userId: Int, recipeId: Int) =
            recipeService.deleteRecipe(userId, recipeId)

        // INGREDIENTS
        suspend fun getIngredients(partial: String) = ingredientsService.getIngredients(partial)
        suspend fun getSubstituteIngredients(name: String) = ingredientsService.getSubstituteIngredients(name)
        suspend fun getIngredientsFromPicture(picture: MultipartFile) = ingredientsService.getIngredientsFromPicture(picture)

        // MENU
        fun getDailyMenu(intolerances: List<Intolerance>, diets: List<Diet>) = menuService.getDailyMenu(intolerances, diets)
    }
}
