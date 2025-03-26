package epicurius.domain

enum class FollowingStatus {
    ACCEPTED,
    PENDING,
    REJECTED;

    companion object {
        fun fromInt(value: Int): Intolerance {
            return Intolerance.entries.getOrNull(value) ?: throw IllegalArgumentException("Invalid Id")
        }
    }
}
