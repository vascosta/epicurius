package epicurius.http.controllers.feed

import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.feed.models.output.FeedOutputModel
import epicurius.http.pipeline.authentication.AuthenticationRefreshHandler
import epicurius.http.pipeline.authentication.addCookie
import epicurius.http.utils.Uris
import epicurius.services.feed.FeedService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class FeedController(
    private val authenticationRefreshHandler: AuthenticationRefreshHandler,
    private val feedService: FeedService
) {

    @GetMapping(Uris.Feed.FEED)
    fun getFeed(
        authenticatedUser: AuthenticatedUser,
        @RequestParam skip: Int,
        @RequestParam limit: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val pagingParams = PagingParams(skip, limit)
        val feed = feedService.getFeed(
            authenticatedUser.user.id,
            authenticatedUser.user.intolerances,
            authenticatedUser.user.diets,
            pagingParams
        )
        return ResponseEntity
            .ok()
            .body(FeedOutputModel(feed))
            .addCookie(response, authenticationRefreshHandler.refreshToken(authenticatedUser.token))
    }
}
