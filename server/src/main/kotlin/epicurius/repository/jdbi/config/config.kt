package epicurius.repository.jdbi.config

import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.recipe.Ingredient
import epicurius.domain.user.User
import epicurius.repository.jdbi.mappers.DietSetMapper
import epicurius.repository.jdbi.mappers.IngredientMapper
import epicurius.repository.jdbi.mappers.IntoleranceSetMapper
import epicurius.repository.jdbi.mappers.JdbiDailyMealPlannerMapper
import epicurius.repository.jdbi.mappers.JdbiRecipeInfoMapper
import epicurius.repository.jdbi.mappers.JdbiRecipeModelMapper
import epicurius.repository.jdbi.mappers.ProductInfoMapper
import epicurius.repository.jdbi.mappers.ProductMapper
import epicurius.repository.jdbi.mappers.UserMapper
import epicurius.repository.jdbi.mealPlanner.models.JdbiDailyMealPlanner
import epicurius.repository.jdbi.recipe.models.JdbiRecipeInfo
import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(IntoleranceSetMapper())
    registerColumnMapper(DietSetMapper())

    registerRowMapper(User::class.java, UserMapper(IntoleranceSetMapper(), DietSetMapper()))
    registerRowMapper(Product::class.java, ProductMapper())
    registerRowMapper(ProductInfo::class.java, ProductInfoMapper())
    registerRowMapper(JdbiRecipeInfo::class.java, JdbiRecipeInfoMapper())
    registerRowMapper(Ingredient::class.java, IngredientMapper())
    registerRowMapper(
        JdbiRecipeModel::class.java,
        JdbiRecipeModelMapper(
            IntoleranceSetMapper(),
            DietSetMapper(),
            IngredientMapper()
        )
    )
    registerRowMapper(JdbiDailyMealPlanner::class.java, JdbiDailyMealPlannerMapper(JdbiRecipeInfoMapper()))

    return this
}
