package android.epicurius.domain.exceptions

class MissingUserToken : RuntimeException("Missing user token")
class AuthenticatedUserNotFound : RuntimeException("Authenticated user not found")
class InvalidToken : RuntimeException("Invalid token")