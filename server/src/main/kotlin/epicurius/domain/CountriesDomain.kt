package epicurius.domain

import org.springframework.stereotype.Component
import java.util.Locale

@Component
class CountriesDomain {
    companion object {
        private val countryMap: List<String> by lazy {
            Locale.getISOCountries().map { it.toString().lowercase() }
        }
    }

    fun checkIfCodeIsValid(countryCode: String): Boolean = countryMap.any { it == countryCode.lowercase() }
}
