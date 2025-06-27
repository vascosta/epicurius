package epicurius.unit.services

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.collection.CollectionType
import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.user.FollowRequestType
import epicurius.http.controllers.collection.models.input.CreateCollectionInputModel
import epicurius.http.controllers.collection.models.input.UpdateCollectionInputModel
import epicurius.http.controllers.fridge.models.input.AddProductInputModel
import epicurius.http.controllers.fridge.models.input.UpdateProductInputModel
import epicurius.http.controllers.mealPlanner.models.input.AddMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateMealPlannerInputModel
import epicurius.http.controllers.recipe.models.input.CreateRecipeInputModel
import epicurius.http.controllers.recipe.models.input.SearchRecipesInputModel
import epicurius.http.controllers.recipe.models.input.UpdateRecipeInputModel
import epicurius.http.controllers.user.models.input.UpdateUserInputModel
import epicurius.unit.EpicuriusUnitTest
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

open class ServiceTest : EpicuriusUnitTest() {

    companion object {
        // USER
        fun createUser(username: String, email: String, country: String, password: String) =
            userService.createUser(username, email, country, password)

        fun getAuthenticatedUser(token: String) = userService.getAuthenticatedUser(token)

        fun getUserProfile(authenticatedUserId: Int, username: String) =
            userService.getUserProfile(authenticatedUserId, username)

        fun getProfilePicture(profilePictureName: String?) = userService.getProfilePicture(profilePictureName)

        fun searchUsers(
            userId: Int,
            partialUsername: String,
            lastUserId: Int?,
            limit: Int
        ) =
            userService.searchUsers(userId, partialUsername, lastUserId, limit)

        fun getFollowers(
            userId: Int,
            lastFollowerId: Int?,
            limit: Int
        ) = userService.getFollowers(userId, lastFollowerId, limit)

        fun getFollowing(
            userId: Int,
            lastFollowingId: Int?,
            limit: Int
        ) = userService.getFollowing(userId, lastFollowingId, limit)

        fun getFollowRequests(userId: Int) = userService.getFollowRequests(userId)

        fun login(username: String? = null, email: String? = null, password: String) =
            userService.login(username, email, password)

        fun logout(userId: Int) = userService.logout(userId)

        fun updateUser(userId: Int, userUpdate: UpdateUserInputModel) =
            userService.updateUser(userId, userUpdate)

        fun updateProfilePicture(userId: Int, profilePictureName: String? = null, profilePicture: MultipartFile? = null) =
            userService.updateProfilePicture(userId, profilePictureName, profilePicture)

        fun resetPassword(email: String, newPassword: String) =
            userService.resetPassword(email, newPassword)

        fun follow(userId: Int, username: String, usernameToFollow: String) = userService.follow(userId, username, usernameToFollow)

        fun unfollow(userId: Int, username: String, usernameToUnfollow: String) = userService.unfollow(userId, username, usernameToUnfollow)

        fun deleteUser(userId: Int) = userService.deleteUser(userId)

        fun cancelFollowRequest(userId: Int, username: String, usernameToCancel: String) =
            userService.followRequest(userId, username, usernameToCancel, FollowRequestType.CANCEL)

        fun acceptFollowRequest(userId: Int, username: String, usernameToAccept: String) =
            userService.followRequest(userId, username, usernameToAccept, FollowRequestType.ACCEPT)

        fun rejectFollowRequest(userId: Int, username: String, usernameToReject: String) =
            userService.followRequest(userId, username, usernameToReject, FollowRequestType.REJECT)

        // FEED
        fun getFeed(
            userId: Int,
            intolerances: List<Intolerance>,
            diets: List<Diet>,
            lastRecipeId: Int?,
            limit: Int
        ) = feedService.getFeed(userId, intolerances, diets, lastRecipeId, limit)

        // FRIDGE
        fun getFridge(userId: Int) = fridgeService.getFridge(userId)

        suspend fun addProduct(userId: Int, productName: AddProductInputModel) =
            fridgeService.addProduct(userId, productName)

        fun updateProductInfo(userId: Int, entryNumber: Int, product: UpdateProductInputModel) =
            fridgeService.updateProductInfo(userId, entryNumber, product)

        fun removeProduct(userId: Int, entryNumber: Int) = fridgeService.removeProduct(userId, entryNumber)

        // RECIPE
        suspend fun createRecipe(authorId: Int, authorName: String, recipeInfo: CreateRecipeInputModel, pictures: Set<MultipartFile>) =
            recipeService.createRecipe(authorId, authorName, recipeInfo, pictures)

        suspend fun getRecipe(recipeId: Int, userId: Int) = recipeService.getRecipe(recipeId, userId)

        suspend fun getUserRecipes(userId: Int, lastRecipeId: Int?, limit: Int) =
            recipeService.getUserRecipes(userId, lastRecipeId, limit)

        fun searchRecipes(userId: Int, form: SearchRecipesInputModel, lastRecipeId: Int?, limit: Int) =
            recipeService.searchRecipes(userId, form, lastRecipeId, limit)

        suspend fun updateRecipe(userId: Int, recipeId: Int, recipeInfo: UpdateRecipeInputModel) =
            recipeService.updateRecipe(userId, recipeId, recipeInfo)

        fun updateRecipePictures(userId: Int, recipeId: Int, pictures: Set<MultipartFile>) =
            recipeService.updateRecipePictures(userId, recipeId, pictures)

        fun deleteRecipe(userId: Int, recipeId: Int) =
            recipeService.deleteRecipe(userId, recipeId)

        // RATE RECIPE
        fun getRecipeRating(userId: Int, recipeId: Int) = rateRecipeService.getRecipeRating(userId, recipeId)

        fun getUserRecipeRating(userId: Int, recipeId: Int) = rateRecipeService.getUserRecipeRating(userId, recipeId)

        fun rateRecipe(userId: Int, recipeId: Int, rating: Int) =
            rateRecipeService.rateRecipe(userId, recipeId, rating)

        fun updateUserRecipeRating(userId: Int, recipeId: Int, rating: Int) =
            rateRecipeService.updateUserRecipeRating(userId, recipeId, rating)

        fun deleteUserRecipeRating(userId: Int, recipeId: Int) =
            rateRecipeService.deleteUserRecipeRating(userId, recipeId)

        // INGREDIENTS
        suspend fun getIngredients(partial: String) = ingredientsService.getIngredients(partial)
        suspend fun getSubstituteIngredients(name: String) = ingredientsService.getSubstituteIngredients(name)
        suspend fun identifyIngredientsInPicture(picture: MultipartFile) = ingredientsService.identifyIngredientsInPicture(picture)

        // MENU
        fun getDailyMenu(
            userId: Int,
            intolerances: List<Intolerance>,
            diets: List<Diet>
        ) = menuService.getDailyMenu(userId, intolerances, diets)

        // COLLECTION
        fun createCollection(ownerId: Int, createCollectionInfo: CreateCollectionInputModel) =
            collectionService.createCollection(ownerId, createCollectionInfo)

        fun getCollection(userId: Int, collectionId: Int) =
            collectionService.getCollection(userId, collectionId)

        fun getCollections(
            userId: Int,
            collectionType: CollectionType,
            lastCollectionId: Int?,
            limit: Int
        ) =
            collectionService.getCollections(userId, collectionType, lastCollectionId, limit)

        fun updateCollection(userId: Int, collectionId: Int, updateCollectionInfo: UpdateCollectionInputModel) =
            collectionService.updateCollection(userId, collectionId, updateCollectionInfo)

        fun addRecipeToCollection(userId: Int, collectionId: Int, recipeId: Int) =
            collectionService.addRecipeToCollection(userId, collectionId, recipeId)

        fun removeRecipeFromCollection(userId: Int, collectionId: Int, recipeId: Int) =
            collectionService.removeRecipeFromCollection(userId, collectionId, recipeId)

        fun deleteCollection(userId: Int, collectionId: Int) = collectionService.deleteCollection(userId, collectionId)

        // MEAL PLANNER
        fun createDailyMealPlanner(userId: Int, date: LocalDate, maxCalories: Int) =
            mealPlannerService.createDailyMealPlanner(userId, date, maxCalories)

        fun getDailyMealPlanner(userId: Int, date: LocalDate) =
            mealPlannerService.getDailyMealPlanner(userId, date)

        fun addRecipeDailyMealPlanner(userId: Int, date: LocalDate, info: AddMealPlannerInputModel) =
            mealPlannerService.addRecipeToDailyMealPlanner(userId, date, info)

        fun updateDailyMealPlanner(userId: Int, date: LocalDate, info: UpdateMealPlannerInputModel) =
            mealPlannerService.updateDailyMealPlanner(userId, date, info)

        fun removeMealTimeDailyMealPlanner(userId: Int, date: LocalDate, mealTime: MealTime) =
            mealPlannerService.removeMealTimeFromDailyMealPlanner(userId, date, mealTime)

        fun deleteDailyMealPlanner(userId: Int, date: LocalDate) =
            mealPlannerService.deleteDailyMealPlanner(userId, date)

        fun getWeeklyMealPlanner(userId: Int) =
            mealPlannerService.getWeeklyMealPlanner(userId)

        fun updateDailyCalories(userId: Int, date: LocalDate, maxCalories: Int) =
            mealPlannerService.updateDailyCalories(userId, date, maxCalories)
    }
}
