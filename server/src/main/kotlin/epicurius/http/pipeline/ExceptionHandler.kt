package epicurius.http.pipeline

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import epicurius.domain.exceptions.AuthorCannotDeleteRating
import epicurius.domain.exceptions.AuthorCannotRateOwnRecipe
import epicurius.domain.exceptions.AuthorCannotUpdateRating
import epicurius.domain.exceptions.CollectionAlreadyExists
import epicurius.domain.exceptions.CollectionNotAccessible
import epicurius.domain.exceptions.CollectionNotFound
import epicurius.domain.exceptions.DailyMealPlannerNotFound
import epicurius.domain.exceptions.FollowRequestAlreadyBeenSent
import epicurius.domain.exceptions.FollowRequestNotFound
import epicurius.domain.exceptions.IncorrectPassword
import epicurius.domain.exceptions.InvalidCountry
import epicurius.domain.exceptions.InvalidCuisineIdx
import epicurius.domain.exceptions.InvalidDietIdx
import epicurius.domain.exceptions.InvalidIngredient
import epicurius.domain.exceptions.InvalidIngredientUnitIdx
import epicurius.domain.exceptions.InvalidIntolerancesIdx
import epicurius.domain.exceptions.InvalidMealPlannerDate
import epicurius.domain.exceptions.InvalidMealTimeIdx
import epicurius.domain.exceptions.InvalidMealTypeIdx
import epicurius.domain.exceptions.InvalidNumberOfRecipePictures
import epicurius.domain.exceptions.InvalidProduct
import epicurius.domain.exceptions.InvalidSelfCancelFollowRequest
import epicurius.domain.exceptions.InvalidSelfFollow
import epicurius.domain.exceptions.InvalidSelfUnfollow
import epicurius.domain.exceptions.InvalidToken
import epicurius.domain.exceptions.MealPlannerAlreadyExists
import epicurius.domain.exceptions.MealTimeAlreadyExistsInPlanner
import epicurius.domain.exceptions.NotTheCollectionOwner
import epicurius.domain.exceptions.NotTheRecipeAuthor
import epicurius.domain.exceptions.PasswordsDoNotMatch
import epicurius.domain.exceptions.PictureNotFound
import epicurius.domain.exceptions.ProductIsAlreadyOpen
import epicurius.domain.exceptions.ProductNotFound
import epicurius.domain.exceptions.RecipeAlreadyInCollection
import epicurius.domain.exceptions.RecipeIsInvalidForMealTime
import epicurius.domain.exceptions.RecipeNotAccessible
import epicurius.domain.exceptions.RecipeNotFound
import epicurius.domain.exceptions.UnauthorizedException
import epicurius.domain.exceptions.UserAlreadyBeingFollowed
import epicurius.domain.exceptions.UserAlreadyExists
import epicurius.domain.exceptions.UserAlreadyLoggedIn
import epicurius.domain.exceptions.UserAlreadyRated
import epicurius.domain.exceptions.UserHasNotRated
import epicurius.domain.exceptions.UserNotFollowed
import epicurius.domain.exceptions.UserNotFound
import epicurius.http.pipeline.authentication.AuthenticationInterceptor.Companion.WWW_AUTHENTICATE_HEADER
import epicurius.http.pipeline.authentication.RequestTokenProcessor.Companion.SCHEME
import epicurius.http.utils.Problem
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.net.URI

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleValidationException(request: HttpServletRequest, ex: MethodArgumentNotValidException) =
        ex.handle(
            request = request,
            status = HttpStatus.BAD_REQUEST,
            title = "Invalid Argument",
            detail = ex.bindingResult.fieldErrors.firstOrNull()?.let { "${it.field.title()} ${it.defaultMessage}" }
        )

    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class])
    fun handleTypeMismatchException(request: HttpServletRequest, ex: MethodArgumentTypeMismatchException) =
        ex.handle(
            request = request,
            status = HttpStatus.BAD_REQUEST,
            detail = "Invalid argument for parameter ${ex.name}"
        )

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun handleHttpMessageNotReadableException(request: HttpServletRequest, ex: HttpMessageNotReadableException) =
        ex.handle(
            request = request,
            status = HttpStatus.BAD_REQUEST,
            type = "invalid-request-body",
            title = "Invalid request body",
            detail = when (val cause = ex.rootCause) {
                is MismatchedInputException -> "Missing property '${cause.path.first().fieldName}'"
                is JsonParseException -> "Please check the request body and try again."
                else -> null
            }
        )

    @ExceptionHandler(value = [HttpRequestMethodNotSupportedException::class])
    fun handleHttpRequestMethodNotSupportedException(
        request: HttpServletRequest,
        ex: HttpRequestMethodNotSupportedException
    ) =
        ex.handle(
            request = request,
            status = HttpStatus.METHOD_NOT_ALLOWED,
            type = "method-not-allowed",
            title = "Method Not Allowed",
            detail = "The method ${ex.method} is not allowed for the requested resource."
        )

    @ExceptionHandler(value = [MissingServletRequestParameterException::class])
    fun handleMissingRequestValueException(
        request: HttpServletRequest,
        ex: MissingServletRequestParameterException
    ) =
        ex.handle(
            request = request,
            status = HttpStatus.BAD_REQUEST,
            type = "missing-request-value",
            title = "Missing Request Value",
            detail = "The request is missing the required parameter '${ex.parameterName}'"
        )

    @ExceptionHandler(
        value = [
            IllegalArgumentException::class,
            InvalidCountry::class,
            IncorrectPassword::class,
            PasswordsDoNotMatch::class,
            InvalidIntolerancesIdx::class,
            InvalidDietIdx::class,
            InvalidProduct::class,
            InvalidCuisineIdx::class,
            InvalidMealTypeIdx::class,
            InvalidIngredientUnitIdx::class,
            InvalidMealTimeIdx::class,
            InvalidMealPlannerDate::class,
            RecipeIsInvalidForMealTime::class,
            InvalidNumberOfRecipePictures::class,
            InvalidIngredient::class,
            UserHasNotRated::class
        ]
    )
    fun handleBadRequest(request: HttpServletRequest, ex: Exception) =
        ex.handle(
            request = request,
            status = HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(
        value = [
            UnauthorizedException::class,
            InvalidToken::class
        ]
    )
    fun handleUnauthorized(request: HttpServletRequest, ex: Exception): ResponseEntity<*> {
        return ex.handle(
            request = request,
            status = HttpStatus.UNAUTHORIZED,
            headers = HttpHeaders().apply {
                set(WWW_AUTHENTICATE_HEADER, SCHEME)
            }
        )
    }

    @ExceptionHandler(
        value = [
            UserNotFound::class,
            PictureNotFound::class,
            ProductNotFound::class,
            FollowRequestNotFound::class,
            RecipeNotFound::class,
            DailyMealPlannerNotFound::class,
            CollectionNotFound::class,
        ]
    )
    fun handleNotFound(request: HttpServletRequest, ex: Exception) =
        ex.handle(
            request = request,
            status = HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(
        value = [
            RecipeNotAccessible::class,
            AuthorCannotRateOwnRecipe::class,
            AuthorCannotUpdateRating::class,
            AuthorCannotDeleteRating::class,
            NotTheRecipeAuthor::class,
            NotTheCollectionOwner::class,
            CollectionNotAccessible::class,
        ]
    )
    fun handleForbidden(request: HttpServletRequest, ex: Exception): ResponseEntity<Problem> =
        ex.handle(
            request = request,
            status = HttpStatus.FORBIDDEN
        )

    @ExceptionHandler(
        value = [
            UserAlreadyExists::class,
            UserAlreadyLoggedIn::class,
            UserAlreadyBeingFollowed::class,
            UserNotFollowed::class,
            UserAlreadyRated::class,
            RecipeAlreadyInCollection::class,
            CollectionAlreadyExists::class,
            InvalidSelfFollow::class,
            InvalidSelfUnfollow::class,
            InvalidSelfCancelFollowRequest::class,
            FollowRequestAlreadyBeenSent ::class,
            ProductIsAlreadyOpen::class,
            MealTimeAlreadyExistsInPlanner::class,
            MealPlannerAlreadyExists::class
        ]
    )
    fun handleConflict(request: HttpServletRequest, ex: Exception): ResponseEntity<Problem> =
        ex.handle(
            request = request,
            status = HttpStatus.CONFLICT
        )

    @ExceptionHandler(value = [Exception::class])
    fun handleUncaughtException(request: HttpServletRequest, ex: Exception) =
        ex.handle(
            request = request,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            type = "internal-server-error",
            title = "Internal Server Error",
            detail = "Something went wrong, please try again later."
        ).also { ex.printStackTrace() }

    companion object {
        const val PROBLEMS_DOCS_URI = "" // TODO: Add the URI to the documentation

        private fun Exception.handle(
            request: HttpServletRequest,
            status: HttpStatus,
            type: String = toProblemType(),
            title: String = getName(),
            detail: String? = message,
            headers: HttpHeaders? = null
        ): ResponseEntity<Problem> =
            Problem(
                type = URI.create(PROBLEMS_DOCS_URI + type),
                title = title,
                detail = detail,
                instance = URI.create(request.requestURI)
            ).toResponse(status, headers)

        private fun Exception.getName(): String =
            (this::class.simpleName ?: "Unknown")
                .replace("Exception", "")
                .replace(Regex("([a-z])([A-Z])")) { "${it.groupValues[1]} ${it.groupValues[2]}" }

        private fun Exception.toProblemType(): String = getName().replace(" ", "-").lowercase()

        private fun String.title() = replaceFirstChar { it.titlecase() }
    }
}
