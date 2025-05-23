package epicurius.domain.exceptions

import epicurius.domain.recipe.MAX_PICTURES
import epicurius.domain.recipe.MIN_PICTURES

class RecipeNotFound : RuntimeException("Recipe not found")
class NotTheRecipeAuthor : RuntimeException("You are not the author of this recipe")
class RecipeNotAccessible : RuntimeException("Recipe not accessible")

class InvalidCuisineIdx : RuntimeException("Invalid cuisine idx")
class InvalidMealTypeIdx : RuntimeException("Invalid meal type idx")
class InvalidIngredientUnitIdx : RuntimeException("Invalid ingredient unit idx")

class InvalidIngredient(ingredientName: String) : RuntimeException("$ingredientName is not a valid ingredient")
class InvalidNumberOfRecipePictures : RuntimeException("The number of pictures must be at least $MIN_PICTURES and at most $MAX_PICTURES")
