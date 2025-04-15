package epicurius.unit.http;

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.MealType
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.recipe.models.input.UpdateRecipeInputModel
import epicurius.unit.EpicuriusUnitTest
import org.springframework.web.multipart.MultipartFile

open class HttpTest: EpicuriusUnitTest() {

    companion object {
        // USER

        // FRIDGE

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

        fun createRecipe(
            authenticatedUser: AuthenticatedUser,
            createRecipeInputModel: String,
            pictures: List<MultipartFile>
        ) = recipeController.createRecipe(authenticatedUser, createRecipeInputModel, pictures)

        suspend fun updateRecipe(authenticatedUser: AuthenticatedUser, id: Int, updateRecipeInputModel: UpdateRecipeInputModel) =
            recipeController.updateRecipe(authenticatedUser, id, updateRecipeInputModel)

        fun deleteRecipe(authenticatedUser: AuthenticatedUser, id: Int) =
            recipeController.deleteRecipe(authenticatedUser, id)
    }
}
