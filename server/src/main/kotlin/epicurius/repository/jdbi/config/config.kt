package epicurius.repository.jdbi.config

import epicurius.domain.fridge.Product
import epicurius.domain.fridge.ProductInfo
import epicurius.domain.user.User
import epicurius.repository.jdbi.mappers.DietListMapper
import epicurius.repository.jdbi.mappers.IntoleranceListMapper
import epicurius.repository.jdbi.mappers.ProductInfoMapper
import epicurius.repository.jdbi.mappers.ProductMapper
import epicurius.repository.jdbi.mappers.UserMapper
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

    return this
}
