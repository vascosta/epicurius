package epicurius.integration.ingredients

import epicurius.http.controllers.ingredients.models.output.GetIngredientsOutputModel
import epicurius.http.controllers.ingredients.models.output.GetSubstituteIngredientsOutputModel
import epicurius.http.controllers.ingredients.models.output.IdentifyIngredientsInPictureOutputModel
import epicurius.http.media.Uris
import epicurius.integration.EpicuriusIntegrationTest
import epicurius.integration.utils.get
import epicurius.integration.utils.getBody
import epicurius.integration.utils.postMultiPart
import org.springframework.http.HttpStatus
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters

class IngredientsIntegrationTest : EpicuriusIntegrationTest() {

    fun getIngredients(token: String, partial: String) =
        get<GetIngredientsOutputModel>(
            client,
            api("${Uris.Ingredients.INGREDIENTS}?partial=$partial"),
            token = token
        )

    fun getSubstituteIngredients(token: String, name: String) =
        get<GetSubstituteIngredientsOutputModel>(
            client,
            api("${Uris.Ingredients.INGREDIENTS_SUBSTITUTES}?name=$name"),
            token = token
        )

    fun identifyIngredientsInPicture(token: String, picture: MultipartFile): IdentifyIngredientsInPictureOutputModel? {
        val multipartBody = MultipartBodyBuilder().apply {
            part("picture", picture.resource)
        }.build()

        val result = postMultiPart<IdentifyIngredientsInPictureOutputModel>(
            client,
            api(Uris.Ingredients.INGREDIENTS),
            BodyInserters.fromMultipartData(multipartBody),
            responseStatus = HttpStatus.OK,
            token = token
        )

        return getBody(result)
    }
}
