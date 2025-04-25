package epicurius.http.user.models.output

import epicurius.domain.Diet

data class GetUserDietsOutputModel(val diets: Set<Diet>)
