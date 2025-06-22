package android.epicurius.domain

enum class Intolerance(val displayName: String) {
    DAIRY("Dairy"),
    EGG("Egg"),
    GLUTEN("Gluten"),
    GRAIN("Grain"),
    PEANUT("Peanut"),
    SEAFOOD("Seafood"),
    SESAME("Sesame"),
    SHELLFISH("Shellfish"),
    SOY("Soy"),
    SULFITE("Sulfite"),
    TREE_NUT("Tree Nut"),
    WHEAT("Wheat");

    companion object {
        fun fromDisplayName(displayName: String): Intolerance {
            return Intolerance.entries.find { it.displayName.equals(displayName, ignoreCase = true) }
                ?: throw IllegalArgumentException("Intolerance with display name '$displayName' not found")
        }
    }
}
