package epicurius.http.menu.models.out

import epicurius.domain.recipe.RecipeInfo

data class GetDailyMenuOutputModel(val menu: Map<String, RecipeInfo?>)
