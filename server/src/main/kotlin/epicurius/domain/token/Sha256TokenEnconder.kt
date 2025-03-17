package epicurius.domain.token

import java.security.MessageDigest
import java.util.*

class Sha256TokenEncoder : TokenEncoder {

    override fun matches(token: String, validationInfo: String) = hash(token) == validationInfo

    override fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA256")
        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(
                Charsets.UTF_8.encode(input).array()
            )
        )
    }
}