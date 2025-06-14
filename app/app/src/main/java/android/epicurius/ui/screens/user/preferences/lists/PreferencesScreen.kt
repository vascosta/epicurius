package android.epicurius.ui.screens.user.preferences.lists

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.user.preferences.lists.components.PreferencesCheckboxList
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PreferencesScreen(
    onSetIntolerances: (Set<Intolerance>) -> Unit,
    onSetDiets: (Set<Diet>) -> Unit,
    onDone: () -> Unit,
    enableButtons: Boolean,
) {
    var showFirst by rememberSaveable { mutableStateOf(true) }
    var intolerancesCheckboxStates by rememberSaveable {
        mutableStateOf(List(Intolerance.entries.size) { false })
    }
    var dietsCheckboxStates by rememberSaveable {
        mutableStateOf(List(Diet.entries.size) { false })
    }
    var intolerances by rememberSaveable { mutableStateOf(setOf<Intolerance>()) }
    var diets by rememberSaveable { mutableStateOf(setOf<Diet>()) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Preferences",
                enableButtons = enableButtons,
                icon = null
            )
        },
        floatingActionButton = {
            if (showFirst) {
                TextButton(
                    onClick = {
                        if (intolerances.isNotEmpty()) { onSetIntolerances(intolerances) }
                        showFirst = !showFirst
                    }
                ) { Text("Next") }
            } else {
                TextButton(
                    onClick = {
                        if (diets.isNotEmpty()) { onSetDiets(diets) }
                        onDone()
                    }
                ) { Text("Done") }
            }
        },
        content = { paddingValues ->
            AnimatedContent(
                targetState = showFirst,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut())
                }
            ) { targetState ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .background(Color.White)
                        .verticalScroll(rememberScrollState()),
                ) {
                    if (targetState) {
                        PreferencesCheckboxList(
                            title = "Intolerances",
                            description = "Select your food intolerances below:",
                            items = Intolerance.entries,
                            checkboxStates = intolerancesCheckboxStates,
                            onCheckedChange = { idx, isChecked ->
                                intolerancesCheckboxStates =
                                    intolerancesCheckboxStates.toMutableList().apply {
                                        this[idx] = isChecked
                                    }
                                intolerances = if (isChecked) {
                                    intolerances + Intolerance.entries[idx]
                                } else {
                                    intolerances - Intolerance.entries[idx]
                                }
                            },
                            enableButtons = enableButtons,
                            displayName = { it.displayName }
                        )
                    } else {
                        PreferencesCheckboxList(
                            title = "Diets",
                            description = "Select your preferred diets below:",
                            items = Diet.entries,
                            checkboxStates = dietsCheckboxStates,
                            onCheckedChange = { idx, isChecked ->
                                dietsCheckboxStates =
                                    dietsCheckboxStates.toMutableList().apply {
                                        this[idx] = isChecked
                                    }
                                diets = if (isChecked) {
                                    diets + Diet.entries[idx]
                                } else {
                                    diets - Diet.entries[idx]
                                }
                            },
                            enableButtons = enableButtons,
                            displayName = { it.displayName }
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen(
        onSetIntolerances = {},
        onSetDiets = {},
        onDone = {},
        enableButtons = true
    )
}
