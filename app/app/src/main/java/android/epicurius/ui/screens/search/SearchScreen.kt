package android.epicurius.ui.screens.search

import android.epicurius.R
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.search.components.FilterDialog
import android.epicurius.ui.screens.search.components.FiltersIcon
import android.epicurius.ui.screens.search.components.SearchPhotoComponent
import android.epicurius.ui.screens.utils.MultiSelectDropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberField
import android.epicurius.ui.screens.utils.NumberTextField
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.TabComponent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen(onBackButton: () -> Unit = {}) {
    val tabs = listOf("Recipe", "Users")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var showFiltersDialog by remember { mutableStateOf(false) }

    var mealType by remember { mutableStateOf(listOf<String>()) }
    var cuisine by remember { mutableStateOf(listOf<String>()) }
    var intolerances by remember { mutableStateOf(listOf<String>()) }
    var diets by remember { mutableStateOf(listOf<String>()) }

    var preparationTime by remember { mutableStateOf("") }
    var serving by remember { mutableStateOf("") }
    var minCalories by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf("") }
    var minCarbs by remember { mutableStateOf("") }
    var maxCarbs by remember { mutableStateOf("") }
    var minFat by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxProtein by remember { mutableStateOf("") }

    fun resetFilters() {
        mealType = listOf()
        cuisine = listOf()
        intolerances = listOf()
        diets = listOf()
        preparationTime = ""
        serving = ""
        minCalories = ""
        maxCalories = ""
        minCarbs = ""
        maxCarbs = ""
        minFat = ""
        maxFat = ""
        minProtein = ""
        maxProtein = ""
    }

    Scaffold(
        topBar = { TopBar("Search", backButton = true, onBackButton) },
        bottomBar = { BottomBar() },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SearchTextField(
                        text = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    TabComponent(tabs, selectedTabIndex, { selectedTabIndex = it })

                    if (selectedTabIndex == 0)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            FiltersIcon(onClick = { showFiltersDialog = true })
                        }

                    // search results
                }

                if (selectedTabIndex == 0) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        SearchPhotoComponent()
                    }
                }

                if (showFiltersDialog) {
                    FilterDialog(
                        onDismiss = { showFiltersDialog = false },
                        onCancel = {
                            resetFilters()
                            showFiltersDialog = false
                        },
                        mealType = mealType,
                        onMealTypeChange = { mealType = it },
                        cuisine = cuisine,
                        onCuisineChange = { cuisine = it },
                        intolerances = intolerances,
                        onIntolerancesChange = { intolerances = it },
                        diets = diets,
                        onDietsChange = { diets = it },
                        preparationTime = preparationTime,
                        onPreparationTimeChange = { preparationTime = it },
                        servings = serving,
                        onServingsChange = { serving = it },
                        minCalories = minCalories,
                        onMinCaloriesChange = { minCalories = it },
                        maxCalories = maxCalories,
                        onMaxCaloriesChange = { maxCalories = it },
                        minCarbs = minCarbs,
                        onMinCarbsChange = { minCarbs = it },
                        maxCarbs = maxCarbs,
                        onMaxCarbsChange = { maxCarbs = it },
                        minFat = minFat,
                        onMinFatChange = { minFat = it },
                        maxFat = maxFat,
                        onMaxFatChange = { maxFat = it },
                        minProtein = minProtein,
                        onMinProteinChange = { minProtein = it },
                        maxProtein = maxProtein,
                        onMaxProteinChange = { maxProtein = it }
                    )
                }
            }
        },
        containerColor = Color.White
    )
}

@Preview
@Composable
fun SearchUserScreenPreview() {
    SearchScreen()
}
