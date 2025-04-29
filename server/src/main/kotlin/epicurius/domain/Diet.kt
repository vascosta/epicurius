package epicurius.domain

import epicurius.domain.exceptions.InvalidDietIdx

enum class Diet {
    GLUTEN_FREE,
    KETOGENIC,
    VEGETARIAN,
    LACTO_VEGETARIAN,
    OVO_VEGETARIAN,
    VEGAN,
    PESCETARIAN,
    PALEO,
    PRIMAL,
    LOW_FODMAP,
    WHOLE30;

    companion object {
        fun fromInt(value: Int): Diet {
            return Diet.entries.getOrNull(value) ?: throw InvalidDietIdx()
        }
    }
}
