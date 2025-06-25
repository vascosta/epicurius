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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun SettingsDialog(
    title: String,
    userInfo: UserInfo,
    onDismissRequest: () -> Unit = {},
    onConfirm: (
        name: String?,
        email: String?,
        country: String?,
        password: String?,
        confirmPassword: String?,
        privacy: Boolean?,
        intolerances: Set<Intolerance>?,
        diets: Set<Diet>?
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    enableButtons: Boolean
) {
    var name by remember { mutableStateOf(userInfo.name) }
    var email by remember { mutableStateOf(userInfo.email) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var privacy by remember { mutableStateOf(userInfo.privacy) }
    var country by remember { mutableStateOf(userInfo.country) }
    var intolerances by remember { mutableStateOf(userInfo.intolerances.map { it.displayName }) }
    var diets by remember { mutableStateOf(userInfo.diets.map { it.displayName }) }

    val txt = title.removePrefix("Change ").lowercase()

    AlertDialog(
        onDismissRequest = { if (enableButtons) onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name.takeIf { name != "" && name != userInfo.name },
                        email.takeIf { email != "" && email != userInfo.email },
                        country.takeIf { country != userInfo.country },
                        password.takeIf { password != "" },
                        confirmPassword.takeIf { confirmPassword != "" },
                        privacy.takeIf { privacy != userInfo.privacy },
                        intolerances.map {
                            Intolerance.valueOf(
                                it.uppercase().replace(Regex("[\\s-]"), "_")
                            )
                        }.takeIf { it != userInfo.intolerances }?.toSet(),
                        diets.map {
                            Diet.valueOf(
                                it.uppercase().replace(Regex("[\\s-]"), "_")
                            )
                        }.takeIf { it != userInfo.diets}?.toSet()
                    )
                    onDismissRequest()
                },
                enabled = enableButtons
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                enabled = enableButtons
            ) { Text("Cancel") }
        },
        title = { Text(title) },
        text = {
            Column {
                when (txt) {
                    "name" -> {
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            enabled = enableButtons,
                            label = "New $txt"
                        )
                    }
                    "email" -> {
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            enabled = enableButtons,
                            label = "New $txt",
                        )
                    }
                    "password" -> {
                        PasswordTextField(
                            value = password,
                            onValueChange = { password = it },
                            enabled = enableButtons,
                            label = "New $txt"
                        )
                        PasswordTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            enabled = enableButtons,
                            label = "Confirm new $txt"
                        )
                    }
                    "country" -> {
                        val countryCodes = remember { Locale.getISOCountries().sorted() }
                        DropdownMenuComponent(
                            options = countryCodes,
                            value = country,
                            onValueChange = { country = it },
                            modifier = Modifier.padding(5.dp),
                            enabled = enableButtons,
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
                            enabled = enableButtons,
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
                            enabled = enableButtons,
                            label = "Diets",
                        )
                    }
                    else -> { Text("Unknown setting") }
                }
            }
        }
    )
}