package android.epicurius.ui.screens.search.general

import android.Manifest
import android.epicurius.domain.user.SearchUser
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.search.components.ConfirmIngredientsDialog
import android.epicurius.ui.screens.search.components.FilterDialog
import android.epicurius.ui.screens.search.components.FiltersIcon
import android.epicurius.ui.screens.search.components.SearchPhotoComponent
import android.epicurius.ui.screens.user.components.UserBox
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loading
import android.epicurius.ui.screens.utils.SearchTextField
import android.epicurius.ui.screens.utils.TabComponent
import android.epicurius.ui.screens.utils.apiSuccess
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    usersResultState: LoadState<List<SearchUser>>,
    onBackButton: () -> Unit = {},
    onRecipeSearch: (searchQuery: String) -> Unit = {},
    onSearchUsers: (name: String) -> Unit,
    onSearchUsersClear: () -> Unit,
    onCamera: () -> Unit = {},
    onIdentifyIngredientsInPicture: (ByteArray) -> Unit = {},
    onConfirm: (List<String>) -> Unit = { _ -> },
    onLoadMoreSearchedUsers: (name: String) -> Unit,
    enableButtons: Boolean
) {
    val context = LocalContext.current

    val tabs = listOf("Recipe", "Users")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var searchUsersQuery by remember { mutableStateOf("") }

    var showFiltersDialog by remember { mutableStateOf(false) }

    var mealType by remember { mutableStateOf(listOf<String>()) }
    var cuisine by remember { mutableStateOf(listOf<String>()) }
    var intolerances by remember { mutableStateOf(listOf<String>()) }
    var diets by remember { mutableStateOf(listOf<String>()) }
    var preparationTime by remember { mutableStateOf("") }
    var serving by remember { mutableStateOf("") }
    var minCalories by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxProtein by remember { mutableStateOf("") }
    var minFat by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var minCarbs by remember { mutableStateOf("") }
    var maxCarbs by remember { mutableStateOf("") }

    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                selectedImageBytes = bytes
                onIdentifyIngredientsInPicture(bytes)
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    var showGalleryAccessDialog by remember { mutableStateOf(false) }
    val galleryPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
        else
            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var showConfirmIngredientsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Search",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = enableButtons
            )
        },
        bottomBar = { BottomBar(buttonsEnable = enableButtons && usersResultState !is Loading) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchTextField(
                    text = searchQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onSearchQueryChange = { searchQuery = it },
                    onIconClick = {
                        if (selectedTabIndex == 0) onRecipeSearch(searchQuery)
                        else {
                            onSearchUsersClear()
                            onSearchUsers(searchQuery)
                            searchUsersQuery = searchQuery
                        }
                    },
                    enableButtons = enableButtons
                )

                TabComponent(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )

                if (selectedTabIndex == 0) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        FiltersIcon(onClick = { showFiltersDialog = true })
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                    SearchPhotoComponent(
                        onCamera,
                        onUpload = {
                            when {
                                galleryPermissionState.status.isGranted -> {
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                    showConfirmIngredientsDialog = true
                                }
                                galleryPermissionState.status.shouldShowRationale -> {
                                    showGalleryAccessDialog = true
                                }
                                else -> showGalleryAccessDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LoadStateRenderer(
                        loadState = usersResultState,
                        content = { usersResult ->
                            usersResult.forEach { user ->
                                UserBox(
                                    user,
                                    onUserProfileRequest = {},
                                    enableButtons = enableButtons
                                )
                            }
                            Button(
                                onClick = { onLoadMoreSearchedUsers(searchUsersQuery) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                enabled = enableButtons
                            ) { Text("Load More") }
                        }
                    )
                }

                if (showFiltersDialog) {
                    FilterDialog(
                        onDismiss = { showFiltersDialog = false },
                        onCancel = { showFiltersDialog = false },
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
                        onMaxProteinChange = { maxProtein = it },
                        true
                    )
                }
                if (showGalleryAccessDialog) {
                    LaunchedEffect(Unit) {
                        galleryPermissionState.launchPermissionRequest()
                    }
                }
                if (showConfirmIngredientsDialog && selectedImageBytes != null) {
                    ConfirmIngredientsDialog(
                        ingredients = listOf(),
                        onConfirm = onConfirm,
                        onDismiss = { showConfirmIngredientsDialog = false }
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
    SearchScreen(
        usersResultState = apiSuccess(emptyList()),
        onSearchUsers = {},
        onSearchUsersClear = {},
        onLoadMoreSearchedUsers = {},
        enableButtons = true
    )
}
