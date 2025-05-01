package epicurius.unit.repository.spoonacular

import epicurius.unit.repository.RepositoryTest

open class SpoonacularRepositoryTest : RepositoryTest() {

    companion object {
        suspend fun getIngredients(partialName: String) = sm.spoonacularRepository.getIngredients(partialName)
        suspend fun getSubstituteIngredients(name: String) = sm.spoonacularRepository.getSubstituteIngredients(name)
    }
}
