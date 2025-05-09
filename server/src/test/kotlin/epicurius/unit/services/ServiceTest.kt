package epicurius.unit.services

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.user.FollowRequestType
import epicurius.http.controllers.collection.models.input.CreateCollectionInputModel
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import epicurius.http.controllers.fridge.models.input.ProductInputModel
import epicurius.http.controllers.fridge.models.input.UpdateProductInputModel
import epicurius.http.controllers.recipe.models.input.CreateRecipeInputModel
import epicurius.http.controllers.recipe.models.input.SearchRecipesInputModel
import epicurius.http.controllers.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.controllers.user.models.input.UpdateUserInputModel
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

        fun logout(userId: Int) = userService.logout(userId)

        fun updateUser(userId: Int, userUpdate: UpdateUserInputModel) =
            userService.updateUser(userId, userUpdate)

        fun updateProfilePicture(userId: Int, profilePictureName: String? = null, profilePicture: MultipartFile? = null) =
            userService.updateProfilePicture(userId, profilePictureName, profilePicture)

        fun resetPassword(email: String, newPassword: String, confirmPassword: String) =
            userService.resetPassword(email, newPassword, confirmPassword)

        fun follow(userId: Int, username: String, usernameToFollow: String) = userService.follow(userId, username, usernameToFollow)

        fun unfollow(userId: Int, username: String, usernameToUnfollow: String) = userService.unfollow(userId, username, usernameToUnfollow)

        fun cancelFollowRequest(userId: Int, username: String, usernameToRequest: String) =
            userService.followRequest(userId, username, usernameToRequest, FollowRequestType.CANCEL)

        fun refreshUserToken(oldToken: String) = userService.refreshUserToken(oldToken)

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

        // RATE RECIPE
        fun getRecipeRate(username: String, recipeId: Int) = rateRecipeService.getRecipeRate(username, recipeId)

        fun rateRecipe(userId: Int, username: String, recipeId: Int, rating: Int) =
            rateRecipeService.rateRecipe(userId, username, recipeId, rating)

        fun updateRecipeRate(userId: Int, username: String, recipeId: Int, rating: Int) =
            rateRecipeService.updateRecipeRate(userId, username, recipeId, rating)

        fun deleteRecipeRate(userId: Int, username: String, recipeId: Int) =
            rateRecipeService.deleteRecipeRate(userId, username, recipeId)

        // INGREDIENTS
        suspend fun getIngredients(partial: String) = ingredientsService.getIngredients(partial)
        suspend fun getSubstituteIngredients(name: String) = ingredientsService.getSubstituteIngredients(name)
        suspend fun getIngredientsFromPicture(picture: MultipartFile) = ingredientsService.getIngredientsFromPicture(picture)

        // MENU
        fun getDailyMenu(intolerances: List<Intolerance>, diets: List<Diet>) = menuService.getDailyMenu(intolerances, diets)

        // COLLECTION
        fun createCollection(ownerId: Int, createCollectionInfo: CreateCollectionInputModel) =
            collectionService.createCollection(ownerId, createCollectionInfo)

        fun getCollection(userId: Int, username: String, collectionId: Int) =
            collectionService.getCollection(userId, username, collectionId)

        fun updateCollection(userId: Int, collectionId: Int, updateCollectionInfo: UpdateCollectionInputModel) =
            collectionService.updateCollection(userId, collectionId, updateCollectionInfo)

        fun addRecipeToCollection(userId: Int, username: String, collectionId: Int, recipeId: Int) =
            collectionService.addRecipeToCollection(userId, username, collectionId, recipeId)

        fun removeRecipeFromCollection(userId: Int, collectionId: Int, recipeId: Int) =
            collectionService.removeRecipeFromCollection(userId, collectionId, recipeId)

        fun deleteCollection(userId: Int, collectionId: Int) = collectionService.deleteCollection(userId, collectionId)
    }
}
