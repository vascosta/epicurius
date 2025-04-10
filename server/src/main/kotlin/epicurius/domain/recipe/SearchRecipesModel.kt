package epicurius.domain.recipe

data class SearchRecipesModel(
    val name: String?,
    val cuisine: Int?,
    val mealType: Int?,
    val ingredients: List<String>?,
    val intolerances: List<Int>?,
    val diets: List<Int>?,
    val minCalories: Int?,
    val maxCalories: Int?,
    val minCarbs: Int?,
    val maxCarbs: Int?,
    val minFat: Int?,
    val maxFat: Int?,
    val minProtein: Int?,
    val maxProtein: Int?,
    val minTime: Int?,
    val maxTime: Int?,
    val maxResults: Int = 10,
)
