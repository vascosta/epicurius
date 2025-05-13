package epicurius.repository.jdbi.token

import org.jdbi.v3.core.Jdbi
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TokenScheduler(private val jdbi: Jdbi) {

    @Scheduled(cron = "0 0 0 * * *")
    fun scheduleDeleteToken() {
        jdbi.inTransaction<Unit, Exception> { handle ->
            handle.createUpdate(
                """
                    DELETE FROM dbo.token
                    WHERE current_date - last_used > 30
                """
            ).execute()
        }
    }
}
