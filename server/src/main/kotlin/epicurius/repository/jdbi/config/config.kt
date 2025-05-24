package epicurius.repository.jdbi.config

import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.recipe.Ingredient
import epicurius.domain.user.User
import epicurius.repository.jdbi.collection.models.JdbiCollectionModel
import epicurius.repository.jdbi.collection.models.JdbiCollectionProfileModel
import epicurius.repository.jdbi.mappers.DietListMapper
import epicurius.repository.jdbi.mappers.recipe.IngredientMapper
import epicurius.repository.jdbi.mappers.IntoleranceListMapper
import epicurius.repository.jdbi.mappers.collection.JdbiCollectionMapper
import epicurius.repository.jdbi.mappers.collection.JdbiCollectionProfileMapper
import epicurius.repository.jdbi.mappers.mealPlanner.JdbiDailyMealPlannerRowMapper
import epicurius.repository.jdbi.mappers.recipe.JdbiRecipeInfoMapper
import epicurius.repository.jdbi.mappers.recipe.JdbiRecipeModelMapper
import epicurius.repository.jdbi.mappers.mealPlanner.ProductInfoMapper
import epicurius.repository.jdbi.mappers.mealPlanner.ProductMapper
import epicurius.repository.jdbi.mappers.user.UserMapper
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlannerRow
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(IntoleranceListMapper())
    registerColumnMapper(DietListMapper())

    registerRowMapper(User::class.java, UserMapper(IntoleranceListMapper(), DietListMapper()))
    registerRowMapper(Product::class.java, ProductMapper())
    registerRowMapper(ProductInfo::class.java, ProductInfoMapper())
    registerRowMapper(JdbiRecipeInfo::class.java, JdbiRecipeInfoMapper())
    registerRowMapper(Ingredient::class.java, IngredientMapper())
    registerRowMapper(
        JdbiRecipeModel::class.java,
        JdbiRecipeModelMapper(
            IntoleranceListMapper(),
            DietListMapper(),
            IngredientMapper()
        )
    )
    registerRowMapper(JdbiDailyMealPlannerRow::class.java, JdbiDailyMealPlannerRowMapper(JdbiRecipeInfoMapper()))
    registerRowMapper(JdbiCollectionModel::class.java, JdbiCollectionMapper(JdbiRecipeInfoMapper()))
    registerRowMapper(JdbiCollectionProfileModel::class.java, JdbiCollectionProfileMapper())

    return this
}
