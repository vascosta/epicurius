package epicurius.integration.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.recipe.Cuisine
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.IngredientUnit
import epicurius.domain.recipe.Instructions
import epicurius.domain.recipe.MealType
import epicurius.domain.recipe.Recipe
import epicurius.domain.user.AuthenticatedUser
import epicurius.domain.user.FollowRequestType
import epicurius.http.controllers.recipe.models.input.CreateRecipeInputModel
import epicurius.http.controllers.recipe.models.output.CreateRecipeOutputModel
import epicurius.http.controllers.recipe.models.output.GetRecipeOutputModel
import epicurius.http.controllers.recipe.models.output.GetUserRecipesOutputModel
import epicurius.http.controllers.recipe.models.output.SearchRecipesOutputModel
import epicurius.http.controllers.recipe.models.output.UpdateRecipeOutputModel
import epicurius.http.controllers.recipe.models.output.UpdateRecipePicturesOutputModel
import epicurius.http.media.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.delete
import epicurius.integration.utils.get
import epicurius.integration.utils.getBody
import epicurius.integration.utils.patch
import epicurius.integration.utils.patchMultiPart
import epicurius.integration.utils.postMultiPart
import epicurius.utils.createTestRecipe
import epicurius.utils.createTestUser
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters

class RecipeIntegrationTest : EpicuriusIntegrationTest() {

    val createRecipeInputModel = CreateRecipeInputModel(
        name = "Pastel de nata",
        description = "A delicious Portuguese dessert",
        servings = 4,
        preparationTime = 30,
        cuisine = Cuisine.MEDITERRANEAN,
        mealType = MealType.DESSERT,
        intolerances = setOf(Intolerance.EGG, Intolerance.DAIRY, Intolerance.GLUTEN),
        diets = setOf(Diet.LACTO_VEGETARIAN, Diet.OVO_VEGETARIAN),
        ingredients = listOf(
            Ingredient("Egg", 4.0, IngredientUnit.X),
            Ingredient("Sugar", 200.0, IngredientUnit.G),
            Ingredient("Wheat Flour", 100.0, IngredientUnit.G),
            Ingredient("Milk", 500.0, IngredientUnit.ML),
            Ingredient("Butter", 50.0, IngredientUnit.G)
        ),
        calories = 300,
        protein = 8,
        fat = 10,
        carbs = 40,
        instructions = Instructions(
            mapOf(
                "1" to "Preheat the oven to 200°C.",
                "2" to "In a bowl, mix the eggs, sugar, flour, and milk.",
                "3" to "Pour the mixture into pastry shells.",
                "4" to "Bake for 20 minutes or until golden brown.",
                "5" to "Let cool before serving."
            )
        )
    )

    lateinit var testUser: AuthenticatedUser
    lateinit var testAuthor: AuthenticatedUser
    lateinit var testSearchAuthor: AuthenticatedUser
    lateinit var testRecipe: Recipe

    @BeforeEach
    fun setup() {
        testUser = createTestUser(tm)
        testAuthor = createTestUser(tm)
        testSearchAuthor = createTestUser(tm)
        testRecipe = createTestRecipe(tm, testAuthor.user)
    }

    fun getRecipe(token: String, id: Int) =
        get<GetRecipeOutputModel>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", id.toString())),
            responseStatus = HttpStatus.OK,
            token = token
        )

    fun getUserRecipes(
        token: String,
        lastRecipeId: Int? = null,
        limit: Int = 10
    ) = get<GetUserRecipesOutputModel>(
        client,
        api(Uris.User.USER_RECIPES + "?lastRecipeId=${lastRecipeId ?: ""}&limit=$limit"),
        responseStatus = HttpStatus.OK,
        token = token
    )

    fun searchRecipes(
        token: String,
        name: String? = null,
        cuisine: Set<Cuisine>? = null,
        mealType: Set<MealType>? = null,
        ingredients: Set<String>? = null,
        intolerances: Set<Intolerance>? = null,
        diets: Set<Diet>? = null,
        servings: Int? = null,
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
        showAuthorRecipes: Boolean = false,
        lastRecipeId: Int? = null,
        limit: Int = 10
    ): GetUserRecipesOutputModel? {
        val params = mutableListOf<String>()

        name?.let { params += "name=$it" }

        if (cuisine != null && cuisine.isNotEmpty()) {
            params += "cuisine=${cuisine.joinToString(",") { it.name }}"
        }

        if (mealType != null && mealType.isNotEmpty()) {
            params += "mealType=${mealType.joinToString(",") { it.name }}"
        }

        if (ingredients != null && ingredients.isNotEmpty()) {
            params += "ingredients=${ingredients.joinToString(",")}"
        }

        if (intolerances != null && intolerances.isNotEmpty()) {
            params += "intolerances=${intolerances.joinToString(",")}"
        }

        if (diets != null && diets.isNotEmpty()) {
            params += "diets=${diets.joinToString(",")}"
        }

        servings?.let { params += "servings=$it" }
        minCalories?.let { params += "minCalories=$it" }
        maxCalories?.let { params += "maxCalories=$it" }
        minCarbs?.let { params += "minCarbs=$it" }
        maxCarbs?.let { params += "maxCarbs=$it" }
        minFat?.let { params += "minFat=$it" }
        maxFat?.let { params += "maxFat=$it" }
        minProtein?.let { params += "minProtein=$it" }
        maxProtein?.let { params += "maxProtein=$it" }
        minTime?.let { params += "minTime=$it" }
        maxTime?.let { params += "maxTime=$it" }
        lastRecipeId?.let { params += "lastRecipeId=$it" }

        params += "showAuthorRecipes=$showAuthorRecipes"
        params += "limit=$limit"

        val query = params.joinToString("&")

        return get<SearchRecipesOutputModel>(
            client,
            api(Uris.Recipe.RECIPES) + "?$query",
            responseStatus = HttpStatus.OK,
            token = token
        )
    }

    fun createRecipe(
        token: String,
        body: CreateRecipeInputModel,
        pictures: List<MultipartFile>
    ): CreateRecipeOutputModel? {
        val multipartBody = MultipartBodyBuilder().apply {
            part("body", body)
            pictures.forEach { picture ->
                part("pictures", picture.resource)
            }
        }

        val result = postMultiPart<CreateRecipeOutputModel>(
            client,
            api(Uris.Recipe.RECIPES),
            BodyInserters.fromMultipartData(multipartBody.build()),
            responseStatus = HttpStatus.CREATED,
            token = token
        )

        return getBody(result)
    }

    fun updateRecipe(
        token: String,
        recipeId: Int,
        name: String? = null,
        description: String? = null,
        servings: Int? = null,
        preparationTime: Int? = null,
        cuisine: Cuisine? = null,
        mealType: MealType? = null,
        intolerances: List<Intolerance>? = null,
        diets: List<Diet>? = null,
        ingredients: List<Ingredient>? = null,
        calories: Int? = null,
        protein: Int? = null,
        fat: Int? = null,
        carbs: Int? = null,
        instructions: Instructions? = null
    ): UpdateRecipeOutputModel? {
        val result = patch<UpdateRecipeOutputModel>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", recipeId.toString())),
            body = mapOf(
                "name" to name,
                "description" to description,
                "servings" to servings,
                "preparationTime" to preparationTime,
                "cuisine" to cuisine?.name,
                "mealType" to mealType?.name,
                "intolerances" to intolerances?.map { it.name },
                "diets" to diets?.map { it.name },
                "ingredients" to ingredients,
                "calories" to calories,
                "protein" to protein,
                "fat" to fat,
                "carbs" to carbs,
                "instructions" to instructions
            ),
            responseStatus = HttpStatus.OK,
            token = token
        )

        return getBody(result)
    }

    fun updateRecipePictures(
        token: String,
        recipeId: Int,
        pictures: List<MultipartFile>
    ): UpdateRecipePicturesOutputModel? {
        val multipartBody = MultipartBodyBuilder().apply {
            pictures.forEach { picture ->
                part("pictures", picture.resource)
            }
        }

        val result = patchMultiPart<UpdateRecipePicturesOutputModel>(
            client,
            api(Uris.Recipe.RECIPE_PICTURES.replace("{id}", recipeId.toString())),
            BodyInserters.fromMultipartData(multipartBody.build()),
            responseStatus = HttpStatus.OK,
            token = token
        )

        return getBody(result)
    }

    fun deleteRecipe(
        token: String,
        recipeId: Int
    ) {
        delete<Unit>(
            client,
            api(Uris.Recipe.RECIPE.replace("{id}", recipeId.toString())),
            responseStatus = HttpStatus.NO_CONTENT,
            token = token
        )
    }

    fun follow(token: String, username: String) {
        patch<Unit>(
            client,
            api(Uris.User.USER_FOLLOW.replace("{name}", username)),
            body = "",
            token = token
        )
    }

    fun acceptFollowRequest(
        token: String,
        username: String
    ) {
        patch<Unit>(
            client,
            api(Uris.User.USER_FOLLOW_REQUEST.replace("{name}", username) + "?type=${FollowRequestType.ACCEPT}"),
            body = "",
            token = token
        )
    }
}
