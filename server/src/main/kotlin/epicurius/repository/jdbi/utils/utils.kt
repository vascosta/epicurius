package epicurius.repository.jdbi.utils

import epicurius.domain.user.User
import epicurius.repository.mappers.DietListMapper
import epicurius.repository.mappers.IntoleranceListMapper
import epicurius.repository.mappers.UserMapper
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(IntoleranceListMapper())
    registerColumnMapper(DietListMapper())

    registerRowMapper(User::class.java, UserMapper(IntoleranceListMapper(), DietListMapper()))

    return this
}
