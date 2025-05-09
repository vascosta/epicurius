package epicurius.http.controllers.user.models.output

import epicurius.domain.Diet

data class GetUserDietsOutputModel(val diets: List<Diet>)
