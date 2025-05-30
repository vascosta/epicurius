package epicurius.http.controllers.mealPlanner

import epicurius.domain.mealPlanner.MealTime
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.mealPlanner.models.input.AddMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.input.CreateMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateDailyCaloriesInputModel
import epicurius.http.controllers.mealPlanner.models.input.UpdateMealPlannerInputModel
import epicurius.http.controllers.mealPlanner.models.output.DailyMealPlannerOutputModel
import epicurius.http.controllers.mealPlanner.models.output.GetWeeklyMealPlannerOutputModel
import epicurius.http.media.Uris
import epicurius.http.media.Uris.MealPlanner.mealPlanner
import epicurius.http.media.createdHttpResponse
import epicurius.http.media.okHttpResponse
import epicurius.services.mealPlanner.MealPlannerService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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
    fun getWeeklyMealPlanner(
        authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<*> {
        val mealPlanner = mealPlannerService.getWeeklyMealPlanner(authenticatedUser.user.id)
        return okHttpResponse(GetWeeklyMealPlannerOutputModel(mealPlanner.planner))
    }

    @GetMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun getDailyMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
    ): ResponseEntity<*> {
        val dailyMealPlanner = mealPlannerService.getDailyMealPlanner(authenticatedUser.user.id, date)
        return okHttpResponse(DailyMealPlannerOutputModel(dailyMealPlanner))
    }

    @PostMapping(Uris.MealPlanner.PLANNER)
    fun createDailyMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody body: CreateMealPlannerInputModel,
    ): ResponseEntity<*> {
        val dailyMealPlanner = mealPlannerService.createDailyMealPlanner(authenticatedUser.user.id, body.date, body.maxCalories)
        return createdHttpResponse(mealPlanner(body.date), DailyMealPlannerOutputModel(dailyMealPlanner))
    }

    @PostMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun addRecipeToDailyMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
        @Valid @RequestBody body: AddMealPlannerInputModel,
    ): ResponseEntity<*> {
        val planner = mealPlannerService.addRecipeToDailyMealPlanner(
            authenticatedUser.user.id,
            date,
            body
        )
        return createdHttpResponse(mealPlanner(date), DailyMealPlannerOutputModel(planner))
    }

    @PatchMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun updateDailyMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
        @Valid @RequestBody body: UpdateMealPlannerInputModel,
    ): ResponseEntity<*> {
        val mealPlanner = mealPlannerService.updateDailyMealPlanner(
            authenticatedUser.user.id,
            date,
            body
        )
        return okHttpResponse(DailyMealPlannerOutputModel(mealPlanner))
    }

    @PatchMapping(Uris.MealPlanner.CALORIES)
    fun updateDailyCalories(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
        @Valid @RequestBody body: UpdateDailyCaloriesInputModel,
    ): ResponseEntity<*> {
        val dailyMealPlanner = mealPlannerService.updateDailyCalories(authenticatedUser.user.id, date, body.maxCalories)
        return okHttpResponse(DailyMealPlannerOutputModel(dailyMealPlanner))
    }

    @DeleteMapping(Uris.MealPlanner.CLEAN_MEAL_TIME)
    fun removeMealTimeFromDailyMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
        @PathVariable mealTime: MealTime,
    ): ResponseEntity<*> {
        val mealPlanner = mealPlannerService.removeMealTimeFromDailyMealPlanner(authenticatedUser.user.id, date, mealTime)
        return okHttpResponse(DailyMealPlannerOutputModel(mealPlanner))
    }

    @DeleteMapping(Uris.MealPlanner.MEAL_PLANNER)
    fun deleteDailyMealPlanner(
        authenticatedUser: AuthenticatedUser,
        @PathVariable date: LocalDate,
    ): ResponseEntity<*> {
        val mealPlanner = mealPlannerService.deleteDailyMealPlanner(authenticatedUser.user.id, date)
        return okHttpResponse(GetWeeklyMealPlannerOutputModel(mealPlanner.planner))
    }
}
