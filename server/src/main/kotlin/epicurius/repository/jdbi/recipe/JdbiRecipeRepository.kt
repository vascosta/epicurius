package epicurius.repository.jdbi.recipe

import epicurius.domain.recipe.RecipeProfile
import epicurius.domain.recipe.SearchRecipesModel
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiRecipeRepository(private val handle: Handle) : RecipeRepository {

    override fun createRecipe(recipeInfo: JdbiRecipeModel): Int {
        val recipeId = handle.createUpdate(
            """
                INSERT INTO dbo.Recipe (
                name, author_id, date, servings, 
                preparation_time, meal_type, cuisine, intolerances, diets, calories, protein, fat, carbs, pictures_names
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
            .bind("mealType", recipeInfo.mealType.ordinal)
            .bind("cuisine", recipeInfo.cuisine.ordinal)
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

    override fun getRecipe(recipeId: Int): JdbiRecipeModel? {
        TODO("Not yet implemented")
    }

    override fun searchRecipes(userId: Int, form: SearchRecipesModel): List<RecipeProfile> {
        val query = StringBuilder(
            """
                SELECT id, name, cuisine, meal_type, preparation_time, servings
                FROM dbo.Recipe
                WHERE author_id <> :id
            """
        )

        val params = mutableMapOf<String, Any?>()

        params["id"] = userId
        form.name?.let { query.append(" AND name = :name"); params["name"] = "%$it%" }
        form.cuisine?.let { query.append(" AND cuisine = :cuisine"); params["cuisine"] = it.ordinal }
        form.mealType?.let { query.append(" AND meal_type = :meal"); params["meal"] = it.ordinal }
        form.minCalories?.let { query.append(" AND calories >= :minCal"); params["minCal"] = it }
        form.maxCalories?.let { query.append(" AND calories <= :maxCal"); params["maxCal"] = it }
        form.minCarbs?.let { query.append(" AND carbs >= :minCarb"); params["minCarb"] = it }
        form.maxCarbs?.let { query.append(" AND carbs <= :maxCarb"); params["maxCarb"] = it }
        form.minFat?.let { query.append(" AND fat >= :minFat"); params["minFat"] = it }
        form.maxFat?.let { query.append(" AND fat <= :maxFat"); params["maxFat"] = it }
        form.minProtein?.let { query.append(" AND protein >= :minProt"); params["minProt"] = it }
        form.maxProtein?.let { query.append(" AND protein <= :maxProt"); params["maxProt"] = it }
        form.minTime?.let { query.append(" AND preparation_time >= :minTime"); params["minTime"] = it }
        form.maxTime?.let { query.append(" AND preparation_time <= :maxTime"); params["maxTime"] = it }

        // missing ingredients, intolerances, diets, maxResults

        val result = handle.createQuery(query.toString())

        params.forEach { (key, value) -> result.bind(key, value) }

        return result.mapTo<RecipeProfile>().list()
    }

    override fun deleteRecipe(recipeId: Int) {
        handle.createUpdate(
            """
                DELETE FROM dbo.Recipe
                WHERE id = :recipeId
            """
        )
            .bind("recipeId", recipeId)
            .execute()
    }
}
