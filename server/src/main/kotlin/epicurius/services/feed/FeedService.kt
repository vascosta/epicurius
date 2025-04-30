package epicurius.services.feed

import epicurius.domain.PagingParams
import epicurius.domain.picture.PictureDomain.Companion.RECIPES_FOLDER
import epicurius.domain.recipe.RecipeInfo
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.transaction.TransactionManager
import org.springframework.stereotype.Component

@Component
class FeedService(private val tm: TransactionManager, private val cs: CloudStorageManager) {

    fun getFeed(userId: Int, pagingParams: PagingParams): List<RecipeInfo> {
        val recipes = tm.run { it.feedRepository.getFeed(userId, pagingParams) }

        return recipes.map {
            it.toRecipeInfo(cs.pictureRepository.getPicture(it.pictures.first(), RECIPES_FOLDER))
        }
    }
}
