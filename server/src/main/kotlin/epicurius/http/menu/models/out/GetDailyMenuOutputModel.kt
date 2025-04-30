package epicurius.http.menu.models.out

import epicurius.repository.jdbi.recipe.models.JdbiRecipeModel

data class GetDailyMenuOutputModel(val menu: Map<String, JdbiRecipeModel?>)
