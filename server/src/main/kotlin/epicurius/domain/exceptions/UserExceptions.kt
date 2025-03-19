package epicurius.domain.exceptions

sealed class UserException(msg: String) : Exception(msg) {
    class UnauthorizedException(msg: String) : UserException(msg)
    class UserAlreadyExits : UserException("User already exists")
    class UserNotFound : UserException("User not found")
    class UserAlreadyLoggedIn : UserException("User is already logged in")
    class InvalidCountry : UserException("Invalid country")
    class InvalidPassword : UserException("Invalid password")
}