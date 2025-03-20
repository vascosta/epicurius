package epicurius.http.user.models

import epicurius.domain.Intolerance

data class IntolerancesInputModel (
    val intolerances: List<Intolerance>
)