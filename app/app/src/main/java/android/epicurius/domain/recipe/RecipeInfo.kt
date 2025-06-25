package android.epicurius.domain.recipe

import java.util.Base64

data class RecipeInfo(
    val id: Int,
    val name: String,
    val authorUsername: String,
    val rating: Double,
    val cuisine: Cuisine,
    val mealType: MealType,
    val preparationTime: Int,
    val servings: Int,
    val calories: Int? = null,
    val picture: String
) {
    val pictureBytes: ByteArray
        get() = Base64.getDecoder().decode(picture)

}
