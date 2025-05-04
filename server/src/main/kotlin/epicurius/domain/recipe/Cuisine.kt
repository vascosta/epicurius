package epicurius.domain.recipe

import epicurius.domain.exceptions.InvalidCuisineIdx

enum class Cuisine {
    AFRICAN,
    ASIAN,
    AMERICAN,
    BRITISH,
    CAJUN,
    CARIBBEAN,
    CHINESE,
    EASTERN_EUROPEAN,
    EUROPEAN,
    FRENCH,
    GERMAN,
    GREEK,
    INDIAN,
    IRISH,
    ITALIAN,
    JAPANESE,
    JEWISH,
    KOREAN,
    LATIN_AMERICAN,
    MEDITERRANEAN,
    MEXICAN,
    MIDDLE_EASTERN,
    NORDIC,
    SOUTHERN,
    SPANISH,
    THAI,
    VIETNAMESE;

    companion object {
        fun fromInt(value: Int): Cuisine {
            return Cuisine.entries.getOrNull(value) ?: throw InvalidCuisineIdx()
        }
    }
}
