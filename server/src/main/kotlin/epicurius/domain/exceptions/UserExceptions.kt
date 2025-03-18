package epicurius.domain.exceptions

sealed class UserException(msg: String) : Exception(msg) {
    class UnauthorizedException(msg: String) : UserException(msg)
}