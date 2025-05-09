package epicurius.http.controllers.user.models.output

import epicurius.domain.Intolerance

data class GetUserIntolerancesOutputModel(val intolerances: List<Intolerance>)
