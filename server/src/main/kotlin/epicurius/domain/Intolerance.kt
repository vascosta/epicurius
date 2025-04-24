package epicurius.domain

import epicurius.domain.exceptions.InvalidIntolerance
import epicurius.domain.exceptions.InvalidIntolerancesIdx

enum class Intolerance {
    DAIRY,
    EGG,
    GLUTEN,
    GRAIN,
    PEANUT,
    SEAFOOD,
    SESAME,
    SHELLFISH,
    SOY,
    SULFITE,
    TREE_NUT,
    WHEAT;

    companion object {
        fun fromInt(value: Int): Intolerance {
            return Intolerance.entries.getOrNull(value) ?: throw InvalidIntolerancesIdx()
        }
    }
}
