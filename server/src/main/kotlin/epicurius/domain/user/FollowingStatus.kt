package epicurius.domain.user

import epicurius.domain.Intolerance

enum class FollowingStatus {
    ACCEPTED,
    PENDING,
    NOT_FOLLOWING;

    companion object {
        fun fromInt(value: Int): Intolerance {
            return Intolerance.entries.getOrNull(value) ?: throw IllegalArgumentException("Invalid Id")
        }
    }
}
