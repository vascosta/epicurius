package epicurius.services.ingredients

import epicurius.domain.PictureDomain
import epicurius.domain.PictureDomain.Companion.INGREDIENTS_FOLDER
import epicurius.repository.cloudFunction.manager.CloudFunctionManager
import epicurius.repository.cloudStorage.manager.CloudStorageManager
import epicurius.repository.spoonacular.manager.SpoonacularManager
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class IngredientsService(
    private val cs: CloudStorageManager,
    private val sm: SpoonacularManager,
    private val cf: CloudFunctionManager,
    private val pictureDomain: PictureDomain
) {

    suspend fun getIngredientsFromPicture(picture: MultipartFile): List<String> {
        pictureDomain.validatePicture(picture)
        val pictureName = pictureDomain.generatePictureName() + "." + picture.contentType?.substringAfter("/")
        cs.pictureRepository.updatePicture(pictureName, picture, INGREDIENTS_FOLDER)

        val ingredients = cf.cloudFunctionRepository.getIngredientsFromPicture(pictureName)
        val validIngredients = ingredients.filter { ingredient ->
            sm.spoonacularRepository.getProductsList(ingredient).contains(ingredient)
        }

        cs.pictureRepository.deletePicture(pictureName, INGREDIENTS_FOLDER)

        return validIngredients
    }
}
