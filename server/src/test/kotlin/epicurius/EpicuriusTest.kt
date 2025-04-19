package epicurius

import epicurius.config.HttpClientConfigurer
import epicurius.domain.PictureDomain
import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import epicurius.repository.jdbi.config.configureWithAppRequirements
import epicurius.repository.spoonacular.SpoonacularManager
import epicurius.repository.transaction.jdbi.JdbiTransactionManager
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.FileInputStream

open class EpicuriusTest {

    companion object {

        private const val POSTGRES_DATABASE_URL = "jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"

        val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(POSTGRES_DATABASE_URL)
            }
        ).configureWithAppRequirements()

        init {
            jdbi.useHandle<Exception> {
                it.execute("DELETE FROM dbo.calories")
                it.execute("DELETE FROM dbo.meal_planner_recipe")
                it.execute("DELETE FROM dbo.meal_planner")
                it.execute("DELETE FROM dbo.collection")
                it.execute("DELETE FROM dbo.ingredient")
                it.execute("DELETE FROM dbo.recipe_rating")
                it.execute("DELETE FROM dbo.recipe")
                it.execute("DELETE FROM dbo.followers")
                it.execute("DELETE FROM dbo.fridge")
                it.execute("DELETE FROM dbo.user")
            }
        }

        private val httpClient = HttpClientConfigurer()

        val tm = JdbiTransactionManager(jdbi)
        val sm = SpoonacularManager(httpClient)

        val userDomain = UserDomain(BCryptPasswordEncoder(), Sha256TokenEncoder())
        val pictureDomain = PictureDomain()
        val countriesDomain = CountriesDomain()
        val fridgeDomain = FridgeDomain()

        val testPicture =
            MockMultipartFile(
                "test-picture.jpeg",
                "test-picture.jpeg",
                "image/jpeg",
                FileInputStream("src/test/resources/test-picture.jpeg")
            )

        val testPicture2 = MockMultipartFile(
            "test-picture2.jpeg",
            "test-picture2.jpeg",
            "image/jpg",
            FileInputStream("src/test/resources/test-picture2.jpg")
        )
    }
}
