package epicurius.repository.jdbi.recipe

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.recipe.Ingredient
import epicurius.domain.recipe.MealType
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

    override fun getRecipeById(recipeId: Int): JdbiRecipeModel? =
        handle.createQuery(
            """
                SELECT r.id, r.name, r.author_id, r.date, r.servings, r.preparation_time, 
                       r.cuisine, r.meal_type, r.intolerances, r.diets, r.calories, 
                       r.protein, r.fat, r.carbs, r.pictures_names, 
                       i.name AS ingredient_name, i.quantity, i.unit, u.name as author_username
                FROM dbo.Recipe r 
                JOIN dbo.Ingredient i on r.id = i.recipe_id 
                JOIN dbo.user u on r.author_id = u.id
                WHERE r.id = :id
            """
        )
            .bind("id", recipeId)
            .mapTo<JdbiRecipeModel>()
            .firstOrNull()

    override fun getRandomRecipesFromPublicUsers(
        mealType: MealType,
        intolerances: List<Intolerance>,
        diets: List<Diet>,
        limit: Int
    ): List<JdbiRecipeInfo> =
        handle.createQuery(
            """
                SELECT r.id as recipe_id, r.name as recipe_name, r.cuisine, r.meal_type,  
                    r.preparation_time, r.servings, r.pictures_names
                FROM dbo.Recipe r
                JOIN dbo.user u ON u.id = r.author_id
                WHERE u.privacy = false 
                AND r.meal_type = :mealType 
                AND NOT (r.intolerances && :intolerances)
                AND r.diets @> :diets
                ORDER BY RANDOM()
                LIMIT :limit
            """
        )
            .bind("mealType", mealType.ordinal)
            .bind("intolerances", intolerances.map { it.ordinal }.toTypedArray())
            .bind("diets", diets.map { it.ordinal }.toTypedArray())
            .bind("limit", limit)
            .mapTo<JdbiRecipeInfo>()
            .list()

    override fun searchRecipes(userId: Int, form: SearchRecipesModel, pagingParams: PagingParams): List<JdbiRecipeInfo> {
        val query = StringBuilder(
            """
                WITH available_recipes AS (
                    SELECT r.*
                    FROM dbo.Recipe r
                    JOIN dbo.user u ON u.id = r.author_id
                    LEFT JOIN dbo.followers f ON f.user_id = r.author_id AND f.follower_id = :id
                    WHERE u.privacy = false OR f.follower_id IS NOT NULL
                ) 
                SELECT DISTINCT r.id as recipe_id, r.name as recipe_name, r.cuisine, 
                r.meal_type, r.preparation_time, r.servings, r.pictures_names
                FROM available_recipes r JOIN dbo.Ingredient i ON r.id = i.recipe_id
                WHERE r.author_id <> :id
            """
        )

        val params = mutableMapOf<String, Any?>("id" to userId)

        appendSearchConditions(query, params, form)

        if (!form.ingredients.isNullOrEmpty()) appendIngredients(query, form.ingredients, params)

        query.append(
            """
                GROUP BY r.id, r.name, r.cuisine, r.meal_type, r.preparation_time, r.servings, r.pictures_names
            """
        )

        addCondition(query, params, "LIMIT :limit", "limit", pagingParams.limit)
        addCondition(query, params, "OFFSET :skip", "skip", pagingParams.skip)

        val result = handle.createQuery(query.toString())
        params.toMap().forEach { (key, value) -> result.bind(key, value) }

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

    private fun appendSearchConditions(query: StringBuilder, params: MutableMap<String, Any?>, form: SearchRecipesModel) {
        addCondition(
            query,
            params,
            "AND lower(r.name) LIKE lower(:name)",
            "name",
            form.name?.let { "%$it%" }
        )
        addCondition(query, params, "AND r.cuisine = ANY(:cuisine)", "cuisine", form.cuisine?.toTypedArray())
        addCondition(query, params, "AND r.meal_type = ANY(:meal)", "meal", form.mealType?.toTypedArray())
        addCondition(
            query,
            params,
            "AND NOT (r.intolerances && :intolerances)",
            "intolerances",
            form.intolerances?.toTypedArray()
        )
        addCondition(query, params, "AND r.diets @> :diets", "diets", form.diets?.toTypedArray())
        addCondition(query, params, "AND r.calories >= :minCal", "minCal", form.minCalories)
        addCondition(query, params, "AND r.calories <= :maxCal", "maxCal", form.maxCalories)
        addCondition(query, params, "AND r.carbs >= :minCarb", "minCarb", form.minCarbs)
        addCondition(query, params, "AND r.carbs <= :maxCarb", "maxCarb", form.maxCarbs)
        addCondition(query, params, "AND r.fat >= :minFat", "minFat", form.minFat)
        addCondition(query, params, "AND r.fat <= :maxFat", "maxFat", form.maxFat)
        addCondition(query, params, "AND r.protein >= :minProt", "minProt", form.minProtein)
        addCondition(query, params, "AND r.protein <= :maxProt", "maxProt", form.maxProtein)
        addCondition(query, params, "AND r.preparation_time >= :minTime", "minTime", form.minTime)
        addCondition(query, params, "AND r.preparation_time <= :maxTime", "maxTime", form.maxTime)
    }

    private fun appendIngredients(query: StringBuilder, ingredientsList: List<String>, params: MutableMap<String, Any?>) {
        val ingredientsBinding = ingredientsList.indices.joinToString { ":i$it" }

        query.append(" AND lower(i.name) IN ($ingredientsBinding)")

        ingredientsList.forEachIndexed { idx, ingredient -> params["i$idx"] = ingredient.lowercase() }
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
