package epicurius.domain.exceptions

class RecipesAuthorCannotRateSelf : RuntimeException("The author of the recipe cannot rate their own recipe")
class UserAlreadyRated(userId: Int, recipeId: Int) : RuntimeException(
    "User with id $userId has already rated the recipe with id $recipeId"
)