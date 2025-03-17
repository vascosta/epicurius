package epicurius

import epicurius.domain.token.Sha256TokenEncoder
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class EpicuriusApplication {
    @Bean
    fun jdbi(): Jdbi {
        return Jdbi.create(
            PGSimpleDataSource().apply {
                setURL("")
            }
        )
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()
}

fun main(args: Array<String>) {
    runApplication<EpicuriusApplication>(*args)
}
