package epicurius.http.controllers.feed

import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.controllers.feed.models.output.GetUserFeedOutputModel
import epicurius.http.utils.Uris
import epicurius.services.feed.FeedService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class FeedController(private val feedService: FeedService) {

    @GetMapping(Uris.User.USER_FEED)
    fun getUserFeed(
        authenticatedUser: AuthenticatedUser,
        @RequestParam skip: Int,
        @RequestParam limit: Int,
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
            .body(GetUserFeedOutputModel(feed))
    }
}
