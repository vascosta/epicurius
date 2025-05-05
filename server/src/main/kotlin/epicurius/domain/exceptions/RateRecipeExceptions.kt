package epicurius.domain.exceptions

class AuthorCannotRateOwnRecipe : RuntimeException("The author of the recipe cannot rate their own recipe")
class AuthorCannotUpdateRating : RuntimeException("The author of the recipe cannot update their own recipe rate")

class UserAlreadyRated(userId: Int, recipeId: Int) : RuntimeException(
    "User with id $userId has already rated the recipe with id $recipeId"
)
class UserHasNotRated(userId: Int, recipeId: Int) : RuntimeException(
    "User with id $userId has not rated the recipe with id $recipeId yet"
)