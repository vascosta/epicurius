package epicurius

import epicurius.domain.token.Sha256TokenEncoder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@EnableScheduling
@SpringBootApplication
class EpicuriusApplication {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()
}

fun main(args: Array<String>) {
    runApplication<EpicuriusApplication>(*args)
}
