package android.epicurius.ui.screens.user.profile.utils

fun getFlagEmoji(countryCode: String): String {
    return countryCode
        .uppercase()
        .map { char ->
            Character.toChars(0x1F1E6 + (char.code - 'A'.code)).concatToString()
        }
        .joinToString("")
}
