package epicurius.domain

enum class Diet(name: String) {
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
        fun fromInt(value: Int): Diet {
            return Diet.entries.getOrNull(value) ?: throw IllegalArgumentException("Invalid Id")
        }
    }
}