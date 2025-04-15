package epicurius.domain.exceptions

import epicurius.domain.recipe.RecipeDomain.Companion.MAX_PICTURES
import epicurius.domain.recipe.RecipeDomain.Companion.MIN_PICTURES

class RecipeNotFound : Exception("Recipe not found")
class NotTheAuthor : Exception("You are not the author of this recipe")

class InvalidCuisineIdx : Exception("Invalid cuisine idx")
class InvalidMealTypeIdx : Exception("Invalid meal type idx")
class InvalidIngredientUnitIdx : Exception("Invalid ingredient unit idx")
class InvalidNumberOfRecipePictures : Exception("At least $MIN_PICTURES and at most $MAX_PICTURES pictures are required")
