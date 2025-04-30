package epicurius.http.feed

import epicurius.domain.PagingParams
import epicurius.domain.user.AuthenticatedUser
import epicurius.http.feed.models.output.FeedOutputModel
import epicurius.http.utils.Uris
import epicurius.services.feed.FeedService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uris.PREFIX)
class FeedController(val feedService: FeedService) {

    @GetMapping(Uris.Feed.FEED)
    fun getFeed(
        authenticatedUser: AuthenticatedUser,
        @RequestParam skip: Int,
        @RequestParam limit: Int
    ): ResponseEntity<*> {
        val pagingParams = PagingParams(skip, limit)
        val feed = feedService.getFeed(authenticatedUser.user.id, pagingParams)
        return ResponseEntity.ok().body(FeedOutputModel(feed))
    }
}
