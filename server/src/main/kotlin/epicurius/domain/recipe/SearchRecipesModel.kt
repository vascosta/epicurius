package epicurius.domain.recipe

data class SearchRecipesModel(
    val name: String? = null,
    val cuisine: List<Int>? = null,
    val mealType: List<Int>? = null,
    val ingredients: List<String>? = null,
    val intolerances: List<Int>? = null,
    val diets: List<Int>? = null,
    val minCalories: Int? = null,
    val maxCalories: Int? = null,
    val minCarbs: Int? = null,
    val maxCarbs: Int? = null,
    val minFat: Int? = null,
    val maxFat: Int? = null,
    val minProtein: Int? = null,
    val maxProtein: Int? = null,
    val minTime: Int? = null,
    val maxTime: Int? = null
)
