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
        val fullPath = buildString {
            append(request.requestURI)
            request.queryString?.let {
                append("?").append(it)
            }
        }

        logger.info("Incoming Request: method={}, uri={}", request.method, fullPath)
        chain.doFilter(request, response)
    }
}
