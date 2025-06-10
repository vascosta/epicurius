package android.epicurius.ui.screens.user.settings

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.user.UserInfo
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.user.settings.components.SettingsButton
import android.epicurius.ui.screens.user.settings.components.SettingsDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(
    user: UserInfo,
    onBackButton: () -> Unit,
    onFavouritesRequest: () -> Unit,
    onUserUpdate: (
        username: String?,
        email: String?,
        country: String?,
        password: String?,
        confirmPassword: String?,
        privacy: Boolean?,
        intolerances: List<Intolerance>?,
        diets: List<Diet>?
    ) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    buttonsEnable: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }

    val showDialogFor = { title: String ->
        dialogTitle = title
        showDialog = true
    }

    val settingOptions = listOf(
        "Favourites" to { onFavouritesRequest() },
        "Change username" to { showDialogFor("Change Username") },
        "Change email" to { showDialogFor("Change Email") },
        "Change password" to { showDialogFor("Change Password") },
        "Change country" to { showDialogFor("Change Country") },
        "Change privacy" to { showDialogFor("Change Privacy") },
        "Change intolerances" to { showDialogFor("Change Intolerances") },
        "Change diets" to { showDialogFor("Change Diets") }
    )

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Settings",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = buttonsEnable,
                icon = null
            )
        },
        bottomBar = { BottomBar(buttonsEnable = buttonsEnable) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.02f))

                settingOptions.forEach { (label, action) ->
                    SettingsButton(
                        text = label,
                        onClick = action,
                        enabled = buttonsEnable,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsButton(
                        text = "Delete account",
                        onClick = onDeleteAccount,
                        enabled = buttonsEnable
                    )
                    SettingsButton(
                        text = "Logout",
                        onClick = onLogout,
                        enabled = buttonsEnable
                    )
                }

                if (showDialog) {
                    SettingsDialog(
                        title = dialogTitle,
                        user = user,
                        onDismissRequest = { showDialog = false },
                        onConfirm = onUserUpdate,
                        buttonsEnable = buttonsEnable
                    )
                }
            }
        }
    )
}



@Preview
@Composable
fun SettingsPreview() {
    val user = UserInfo(
        name = "User",
        email = "user@email.com",
        country = "PT",
        privacy = true,
        intolerances = listOf(
            Intolerance.GLUTEN,
            Intolerance.DAIRY
        ),
        diets = listOf(
            Diet.GLUTEN_FREE
        ),
        profilePictureName = ""
    )

    SettingsScreen(
        user,
        {},
        {},
        { _, _, _, _, _, _, _, _ -> },
        {},
        {},
        true
    )
}