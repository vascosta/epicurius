package android.epicurius.ui.screens.user.settings.components

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.screens.auth.components.PasswordTextField
import android.epicurius.ui.screens.utils.DropdownMenuComponent
import android.epicurius.ui.screens.utils.MultiSelectDropdownMenuComponent
import android.epicurius.ui.screens.utils.TextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun SettingsDialog(
    title: String,
    user: UserInfo,
    onDismissRequest: () -> Unit,
    onConfirm: (
        username: String?,
        email: String?,
        country: String?,
        password: String?,
        confirmPassword: String?,
        privacy: Boolean?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?
    ) -> Unit,
    buttonsEnable: Boolean
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var privacy by rememberSaveable { mutableStateOf(user.privacy) }
    var country by rememberSaveable { mutableStateOf(user.country) }
    var intolerances by rememberSaveable { mutableStateOf(user.intolerances.map { it.displayName }) }
    var diets by rememberSaveable { mutableStateOf(user.diets.map { it.displayName }) }

    val txt = title.removePrefix("Change ").lowercase()

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(title) },
        text = {
            Column {
                when (txt) {
                    "username" -> {
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "New $txt",
                            enabled = buttonsEnable
                        )
                    }
                    "email" -> {
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "New $txt",
                            enabled = buttonsEnable
                        )
                    }
                    "password" -> {
                        PasswordTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "New $txt",
                            enabled = buttonsEnable
                        )
                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm new $txt",
                            enabled = buttonsEnable
                        )
                    }
                    "country" -> {
                        val countryCodes = remember { Locale.getISOCountries().sorted() }
                        DropdownMenuComponent(
                            options = countryCodes,
                            value = country,
                            onValueChange = { country = it },
                            modifier = Modifier.padding(5.dp),
                            enabled = buttonsEnable,
                            label = "Country"
                        )
                    }
                    "privacy" -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = privacy,
                                onCheckedChange = { privacy = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Private Profile")
                        }
                    }
                    "intolerances" -> {
                        MultiSelectDropdownMenuComponent(
                            options = Intolerance.entries.map { it.displayName },
                            values = intolerances,
                            onValuesChange = { intolerances = it },
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .align(Alignment.CenterHorizontally),
                            enabled = buttonsEnable,
                            label = "Intolerances",
                        )
                    }
                    "diets" -> {
                        MultiSelectDropdownMenuComponent(
                            options = Diet.entries.map { it.displayName },
                            values = diets,
                            onValuesChange = { diets = it },
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .align(Alignment.CenterHorizontally),
                            enabled = buttonsEnable,
                            label = "Diets",
                        )
                    }
                    else -> { Text("Unknown setting") }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        username.takeIf { username != "" },
                        email.takeIf { email != "" },
                        country.takeIf { country != user.country },
                        password.takeIf { password != "" },
                        confirmPassword.takeIf { confirmPassword != "" },
                        privacy.takeIf { privacy != user.privacy },
                        if (intolerances.isEmpty()) null
                        else intolerances.map {
                            Intolerance.valueOf(
                                it.uppercase().replace(Regex("[\\s-]"), "_")
                            )
                        },
                        if (diets.isEmpty()) null
                        else diets.map {
                            Diet.valueOf(
                                it.uppercase().replace(Regex("[\\s-]"), "_")
                            )
                        }
                    )
                    onDismissRequest()
                },
                enabled = buttonsEnable
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = buttonsEnable
            ) { Text("Cancel") }
        }
    )
}