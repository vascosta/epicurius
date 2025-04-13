package epicurius.http.mealPlanner

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.mealPlanner.models.input.CreateMealPlannerInputModel
import epicurius.http.utils.Uris
import epicurius.services.MealPlannerService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class MealPlannerController(private val mealPlannerService: MealPlannerService) {

    @GetMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun getMealPlanner(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val planner = mealPlannerService.getMealPlanner(authenticatedUser.user.id)
        return ResponseEntity.ok().body(planner)
    }

    @PostMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun createMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: CreateMealPlannerInputModel
    ): ResponseEntity<*> {
        mealPlannerService.createMealPlanner(authenticatedUser.user.id, body.date)
        return ResponseEntity.noContent().build<Unit>()
    }
}
