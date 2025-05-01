package epicurius.unit.services.ingredients

import epicurius.unit.services.ServiceTest

open class IngredientsServiceTest : ServiceTest() {

    companion object {
        const val PARTIAL_NAME = "app"

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

        val testSubstituteIngredients = listOf("1 cup quinces", "1 cup pears")
    }
}
