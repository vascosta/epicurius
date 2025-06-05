package epicurius.http.pipeline.logger

import epicurius.http.pipeline.logger.Logger.logger
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class LoggingFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        request as HttpServletRequest
        response as HttpServletResponse
        logger.info(
            "Incoming Request: method={}, uri={}",
            request.method,
            request.requestURI
        )
        chain.doFilter(request, response)
        logger.info(
            "Outgoing Response: status={}, content-type={}",
            response.status,
            response.contentType
        )
    }
}
