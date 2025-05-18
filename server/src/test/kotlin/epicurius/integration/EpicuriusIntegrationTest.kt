package epicurius.integration

import epicurius.EpicuriusTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EpicuriusIntegrationTest : EpicuriusTest() {


    @LocalServerPort
    var port: Int = 0

    var client: WebTestClient = WebTestClient.bindToServer()
        .baseUrl("http://localhost:$port/api")
        .build()

    fun api(path: String): String = "http://localhost:$port/api$path"
}
