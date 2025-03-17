package epicurius.domain.token

interface TokenEncoder {
    fun matches(token: String, validationInfo: String): Boolean
    fun hash(input: String): String
}