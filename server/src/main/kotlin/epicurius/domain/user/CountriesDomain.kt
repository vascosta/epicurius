package epicurius.domain.user

import org.springframework.stereotype.Component
import java.util.Locale

@Component
class CountriesDomain {

    fun checkIfCountryCodeIsValid(countryCode: String): Boolean = countryMap.any { it == countryCode.lowercase() }

    companion object {
        private val countryMap: List<String> by lazy {
            Locale.getISOCountries().map { it.toString().lowercase() }
        }
    }
}
