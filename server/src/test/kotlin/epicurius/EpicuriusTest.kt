package epicurius

import epicurius.domain.fridge.FridgeDomain
import epicurius.domain.picture.PictureDomain
import epicurius.domain.token.Sha256TokenEncoder
import epicurius.domain.user.CountriesDomain
import epicurius.domain.user.UserDomain
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.FileInputStream

open class EpicuriusTest {

    companion object {

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
