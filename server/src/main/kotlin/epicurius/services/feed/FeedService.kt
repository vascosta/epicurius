package epicurius.services.feed

import epicurius.domain.Diet
import epicurius.domain.Intolerance
import epicurius.domain.PagingParams
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.RecipeInfo
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.transaction.TransactionManager
import epicurius.services.feed.models.GetFeedModel
import org.springframework.stereotype.Component

@Component
class FeedService(private val tm: TransactionManager, private val cs: CloudStorageManager) {

    fun getFeed(
        userId: Int,
        intolerances: List<Intolerance>,
        diets: List<Diet>,
        pagingParams: PagingParams
    ): List<RecipeInfo> {
        val recipes = tm.run { it.feedRepository.getFeed(GetFeedModel(userId, intolerances, diets, pagingParams)) }

        return recipes.map {
            it.toRecipeInfo(cs.pictureRepository.getPicture(it.picturesNames.first(), RECIPES_FOLDER))
        }
    }
}
