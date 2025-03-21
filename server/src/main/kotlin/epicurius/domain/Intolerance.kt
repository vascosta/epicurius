package epicurius.domain

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
            return Intolerance.entries.getOrNull(value) ?: throw IllegalArgumentException("Invalid Id")
        }
    }
}