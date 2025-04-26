package epicurius.domain.exceptions

import epicurius.domain.recipe.RecipeDomain.Companion.MAX_PICTURES
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_PICTURES

class RecipeNotFound : RuntimeException("Recipe not found")
class NotTheAuthor : RuntimeException("You are not the author of this recipe")

class InvalidCuisineIdx : RuntimeException("Invalid cuisine idx")
class InvalidMealTypeIdx : RuntimeException("Invalid meal type idx")
class InvalidIngredientUnitIdx : RuntimeException("Invalid ingredient unit idx")

class InvalidIngredient(ingredientName: String) : RuntimeException("$ingredientName is not a valid ingredient")
class InvalidNumberOfRecipePictures : RuntimeException("At least $MIN_PICTURES and at most $MAX_PICTURES pictures are required")
