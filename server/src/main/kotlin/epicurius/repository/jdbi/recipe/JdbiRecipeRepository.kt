package epicurius.repository.jdbi.recipe

import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.jdbi.recipe.contract.RecipeRepository
import epicurius.repository.jdbi.recipe.models.JdbiCreateRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import epicurius.repository.jdbi.recipe.models.JdbiUpdateRecipeModel
import epicurius.repository.jdbi.utils.addCondition
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiRecipeRepository(private val handle: Handle) : RecipeRepository {

    override fun createRecipe(recipeInfo: JdbiCreateRecipeModel): Int {
        val recipeId = handle.createUpdate(
            """
                INSERT INTO dbo.Recipe (
                    name, author_id, date, servings, 
                    preparation_time, meal_type, cuisine, 
                    intolerances, diets, calories, protein, fat, carbs, pictures_names
                )
                VALUES (
                :name, :authorId, :date, :servings, 
                :preparationTime, :mealType, :cuisine, :intolerances, :diets, :calories, :protein, :fat, :carbs, :pictureNames
                )
                RETURNING id
            """
        )
            .bind("name", recipeInfo.name)
            .bind("authorId", recipeInfo.authorId)
            .bind("date", recipeInfo.date)
            .bind("servings", recipeInfo.servings)
            .bind("preparationTime", recipeInfo.preparationTime)
            .bind("mealType", recipeInfo.mealType)
            .bind("cuisine", recipeInfo.cuisine)
            .bind("intolerances", recipeInfo.intolerances.toTypedArray())
            .bind("diets", recipeInfo.diets.toTypedArray())
            .bind("calories", recipeInfo.calories)
            .bind("protein", recipeInfo.protein)
            .bind("fat", recipeInfo.fat)
            .bind("carbs", recipeInfo.carbs)
            .bind("pictureNames", recipeInfo.picturesNames.toTypedArray())
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

        recipeInfo.ingredients.forEach { ingredient ->
            handle.createUpdate(
                """
                    INSERT INTO dbo.ingredient (recipe_id, name, quantity, unit)
                    VALUES (:recipeId, :name, :quantity, :unit)
                """
            )
                .bind("recipeId", recipeId)
                .bind("name", ingredient.name)
                .bind("quantity", ingredient.quantity)
                .bind("unit", ingredient.unit.ordinal)
                .execute()
        }

        return recipeId
    }

    override fun getRecipe(recipeId: Int): JdbiRecipeModel? =
        handle.createQuery(
            """
                SELECT r.id, r.name, r.author_id, r.date, r.servings, r.preparation_time, 
                       r.cuisine, r.meal_type, r.intolerances, r.diets, r.calories, 
                       r.protein, r.fat, r.carbs, r.pictures_names, 
                       i.name AS ingredient_name, i.quantity, i.unit, u.name as author_username
                FROM dbo.Recipe r JOIN dbo.Ingredient i on r.id = i.recipe_id JOIN dbo.user u on r.author_id = u.id
                WHERE r.id = :id
            """
        )
            .bind("id", recipeId)
            .mapTo<JdbiRecipeModel>()
            .firstOrNull()

    override fun searchRecipes(userId: Int, form: SearchRecipesModel): List<JdbiRecipeInfo> {
        val query = StringBuilder(
            """
                SELECT id, name, cuisine, meal_type, preparation_time, servings, pictures_names
                FROM dbo.Recipe
                WHERE author_id <> :id
            """
        )

        val params = mutableMapOf<String, Any?>("id" to userId)

        addCondition(query, params, "AND lower(name) LIKE lower(:name)", "name", form.name?.let { "%$it%" })
        addCondition(query, params, "AND cuisine = :cuisine", "cuisine", form.cuisine)
        addCondition(query, params, "AND meal_type = :meal", "meal", form.mealType)
        addCondition(query, params, "AND intolerances && :intolerances", "intolerances", form.intolerances?.toTypedArray())
        addCondition(query, params, "AND diets && :diets", "diets", form.diets?.toTypedArray())
        addCondition(query, params, "AND calories >= :minCal", "minCal", form.minCalories)
        addCondition(query, params, "AND calories <= :maxCal", "maxCal", form.maxCalories)
        addCondition(query, params, "AND carbs >= :minCarb", "minCarb", form.minCarbs)
        addCondition(query, params, "AND carbs <= :maxCarb", "maxCarb", form.maxCarbs)
        addCondition(query, params, "AND fat >= :minFat", "minFat", form.minFat)
        addCondition(query, params, "AND fat <= :maxFat", "maxFat", form.maxFat)
        addCondition(query, params, "AND protein >= :minProt", "minProt", form.minProtein)
        addCondition(query, params, "AND protein <= :maxProt", "maxProt", form.maxProtein)
        addCondition(query, params, "AND preparation_time >= :minTime", "minTime", form.minTime)
        addCondition(query, params, "AND preparation_time <= :maxTime", "maxTime", form.maxTime)

        val result = handle.createQuery(query.toString())
        params.forEach { (key, value) -> result.bind(key, value) }

        return result.mapTo<JdbiRecipeInfo>().list()
    }

    override fun searchRecipesByIngredients(userId: Int, ingredientsList: List<String>): List<JdbiRecipeInfo> {
        val ingredientsBinding = ingredientsList.indices.joinToString { ":i$it" }
        val query = StringBuilder(
            """
                SELECT r.id, r.name, r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM dbo.Recipe r JOIN dbo.Ingredient i ON r.id = i.recipe_id
                WHERE author_id <> :id AND lower(i.name) IN ($ingredientsBinding)
                GROUP BY r.id, r.name, r.cuisine, r.meal_type, r.preparation_time, r.servings
                ORDER BY COUNT(DISTINCT lower(i.name)) DESC
            """
        )

        val params = mutableMapOf<String, Any?>("id" to userId)

        ingredientsList.forEachIndexed { idx, ingredient -> params["i$idx"] = ingredient.lowercase() }
        params["count"] = ingredientsList.size

        val result = handle.createQuery(query.toString())
        params.forEach { (key, value) -> result.bind(key, value) }

        return result.mapTo<JdbiRecipeInfo>().list()
    }

    override fun updateRecipe(recipeInfo: JdbiUpdateRecipeModel): JdbiRecipeModel {
        if (recipeInfo.ingredients != null) {
            removeIngredients(recipeInfo.id)
            addIngredients(recipeInfo.id, recipeInfo.ingredients)
        }

        return handle.createQuery(
            """
                WITH updated_recipe AS (
                    UPDATE dbo.Recipe
                    SET name = COALESCE(:name, name),
                        servings = COALESCE(:servings, servings),
                        preparation_time = COALESCE(:preparationTime, preparation_time),
                        cuisine = COALESCE(:cuisine, cuisine),
                        meal_type = COALESCE(:mealType, meal_type),
                        intolerances = COALESCE(:intolerances, intolerances),
                        diets = COALESCE(:diets, diets),
                        calories = COALESCE(:calories, calories),
                        protein = COALESCE(:protein, protein),
                        fat = COALESCE(:fat, fat),
                        carbs = COALESCE(:carbs, carbs),
                        pictures_names = COALESCE(:picturesNames, pictures_names)
                    WHERE id = :id
                    RETURNING *
                )
                SELECT ur.*, i.name AS ingredient_name, i.quantity, i.unit, u.name as author_username
                FROM updated_recipe ur
                JOIN dbo.Ingredient i ON i.recipe_id = ur.id
                JOIN dbo.user u ON u.id = ur.author_id;
            """
        )
            .bind("id", recipeInfo.id)
            .bind("name", recipeInfo.name)
            .bind("servings", recipeInfo.servings)
            .bind("preparationTime", recipeInfo.preparationTime)
            .bind("cuisine", recipeInfo.cuisine)
            .bind("mealType", recipeInfo.mealType)
            .bind("intolerances", recipeInfo.intolerances?.toTypedArray())
            .bind("diets", recipeInfo.diets?.toTypedArray())
            .bind("calories", recipeInfo.calories)
            .bind("protein", recipeInfo.protein)
            .bind("fat", recipeInfo.fat)
            .bind("carbs", recipeInfo.carbs)
            .bind("picturesNames", recipeInfo.picturesNames?.toTypedArray())
            .mapTo<JdbiRecipeModel>()
            .first()
    }

    override fun deleteRecipe(recipeId: Int) {
        removeIngredients(recipeId)
        handle.createUpdate(
            """
                DELETE FROM dbo.Recipe
                WHERE id = :recipeId
            """
        )
            .bind("recipeId", recipeId)
            .execute()
    }

    private fun addIngredients(recipeId: Int, ingredients: List<Ingredient>) {
        ingredients.forEach { ingredient ->
            handle.createUpdate(
                """
                    INSERT INTO dbo.Ingredient (recipe_id, name, quantity, unit)
                    VALUES (:recipeId, :name, :quantity, :unit)
                """
            )
                .bind("recipeId", recipeId)
                .bind("name", ingredient.name)
                .bind("quantity", ingredient.quantity)
                .bind("unit", ingredient.unit.ordinal)
                .execute()
        }
    }

    private fun removeIngredients(recipeId: Int) {
        handle.createUpdate(
            """
                DELETE FROM dbo.Ingredient
                WHERE recipe_id = :recipeId
            """
        )
            .bind("recipeId", recipeId)
            .execute()
    }
}
