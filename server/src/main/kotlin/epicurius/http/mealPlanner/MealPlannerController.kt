package epicurius.http.mealPlanner

import epicurius.domain.user.AuthenticatedUser
import epicurius.http.mealPlanner.models.input.AddMealPlannerInputModel
import epicurius.http.mealPlanner.models.input.CreateMealPlannerInputModel
import epicurius.http.mealPlanner.models.input.UpdateMealPlannerInputModel
import epicurius.http.mealPlanner.models.output.MealPlannerOutputModel
import epicurius.http.utils.Uris
import epicurius.services.mealPlanner.MealPlannerService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping(Uris.PREFIX)
class MealPlannerController(private val mealPlannerService: MealPlannerService) {

    @GetMapping(Uris.MealPlanner.PLANNER)
    fun getMealPlanner(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val mealPlanner = mealPlannerService.getMealPlanner(authenticatedUser.user.id)
        return ResponseEntity.ok().body(MealPlannerOutputModel(mealPlanner.planner))
    }

    @PostMapping(Uris.MealPlanner.PLANNER)
    fun createMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: CreateMealPlannerInputModel
    ): ResponseEntity<*> {
        mealPlannerService.createMealPlanner(authenticatedUser.user.id, body.date)
        return ResponseEntity.noContent().build<Unit>()
    }

    @PostMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun addRecipeToMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
        @Valid @RequestBody body: AddMealPlannerInputModel
    ): ResponseEntity<*> {
        val mealPlanner = mealPlannerService.addMealPlanner(authenticatedUser.user.id, date, body)
        return ResponseEntity.ok().body(MealPlannerOutputModel(mealPlanner.planner))
    }

    @PatchMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun updateMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
        @Valid @RequestBody body: UpdateMealPlannerInputModel
    ): ResponseEntity<*> {
        val mealPlanner = mealPlannerService.updateMealPlanner(authenticatedUser.user.id, date, body)
        return ResponseEntity.ok().body(MealPlannerOutputModel(mealPlanner.planner))
    }
}
