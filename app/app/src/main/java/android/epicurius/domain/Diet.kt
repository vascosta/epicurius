package android.epicurius.domain

enum class Diet(val displayName: String) {
    GLUTEN_FREE("Gluten Free"),
    KETOGENIC("Ketogenic"),
    VEGETARIAN("Vegetarian"),
    LACTO_VEGETARIAN("Lacto-Vegetarian"),
    OVO_VEGETARIAN("Ovo-Vegetarian"),
    VEGAN("Vegan"),
    PESCETARIAN("Pescetarian"),
    PALEO("Paleo"),
    PRIMAL("Primal"),
    LOW_FODMAP("Low FODMAP"),
    WHOLE30("Whole30");

    companion object {
        fun fromDisplayName(displayName: String): Diet {
            return Diet.entries.find { it.displayName.equals(displayName, ignoreCase = true) }
                ?: throw IllegalArgumentException("Diet with display name '$displayName' not found")
        }
    }
}
