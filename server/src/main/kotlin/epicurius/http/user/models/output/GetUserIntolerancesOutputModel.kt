package epicurius.http.user.models.output

import epicurius.domain.Intolerance

data class GetUserIntolerancesOutputModel(val intolerances: Set<Intolerance>)
