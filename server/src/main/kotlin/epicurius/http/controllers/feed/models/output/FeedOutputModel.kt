package epicurius.http.controllers.feed.models.output

import epicurius.domain.recipe.RecipeInfo

data class FeedOutputModel(val feed: List<RecipeInfo>)
