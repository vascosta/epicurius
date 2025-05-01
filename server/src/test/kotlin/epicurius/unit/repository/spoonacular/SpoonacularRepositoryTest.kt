package epicurius.unit.repository.spoonacular

import epicurius.unit.repository.RepositoryTest

open class SpoonacularRepositoryTest: RepositoryTest() {

    companion object {
        val testIngredients = listOf(
            "apple",
            "applesauce",
            "apple juice",
            "apple cider",
            "apple jelly",
            "apple butter",
            "apple pie spice",
            "apple pie filling",
            "apple cider vinegar",
            "applewood smoked bacon"
        )

        suspend fun getIngredients(partialName: String) = sm.spoonacularRepository.getIngredients(partialName)
    }
}