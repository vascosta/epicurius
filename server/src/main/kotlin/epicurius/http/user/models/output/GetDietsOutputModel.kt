package epicurius.http.user.models.output

import epicurius.domain.Diet

data class GetDietsOutputModel(val diet: Set<Diet>)
