package epicurius.domain

data class PagingParams(
    val skip: Int = Skip.DEFAULT,
    val limit: Int = Limit.DEFAULT,
) {

    init {
        require(skip >= Skip.DEFAULT) { "Skip must be greater than or equal to ${Skip.DEFAULT}" }
        require(limit in Limit.DEFAULT..Limit.MAX_VALUE) { "Limit must be between ${Limit.DEFAULT} and ${Limit.MAX_VALUE}" }
    }

    companion object {
        private object Skip {
            const val DEFAULT = 0
        }

        private object Limit {
            const val DEFAULT = 1
            const val MAX_VALUE = 10
        }
    }
}
