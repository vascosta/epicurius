package epicurius.http.controllers.feed.models.output

import epicurius.domain.recipe.RecipeInfo

data class GetUserFeedOutputModel(val feed: List<RecipeInfo>)
