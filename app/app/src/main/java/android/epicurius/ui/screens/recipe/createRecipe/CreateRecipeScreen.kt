package android.epicurius.ui.screens.recipe.createRecipe

import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.Picture
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.ui.navigation.BottomBar
import android.epicurius.ui.navigation.TopBar
import android.epicurius.ui.screens.recipe.createRecipe.components.DividerComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.IngredientsComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.InstructionsComponent
import android.epicurius.ui.screens.recipe.createRecipe.components.NutritionalInfoComponent
import android.epicurius.ui.screens.utils.DropdownMenuComponent
import android.epicurius.ui.screens.utils.FormTextField
import android.epicurius.ui.screens.utils.LoadingSpinner
import android.epicurius.ui.screens.utils.MultiSelectDropdownMenuComponent
import android.epicurius.ui.screens.utils.NumberLineTextField
import android.epicurius.ui.screens.utils.NumberTextField
import android.epicurius.ui.screens.utils.isValidForNumberTextField
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

data class IngredientComponent(
    val name: String = "",
    val quantity: String = "",
    val unit: String = ""
) {
    fun toIngredient(): Ingredient {
        return Ingredient(
            name = name,
            quantity = quantity.toDouble(),
            unit = IngredientUnit.fromString(unit)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateRecipeScreen(
    onBackButton: () -> Unit,
    onCreateRecipe: (
        name: String,
        description: String,
        servings: Int,
        preparationTime: Int,
        cuisine: Cuisine,
        mealType: MealType,
        intolerances: Set<Intolerance>,
        diets: Set<Diet>,
        ingredients: List<Ingredient>,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?,
        instructions: Instructions,
        picturesBytes: List<ByteArray>
    ) -> Unit,
    buttonsEnable: Boolean
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var preparationTime by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }
    var cuisine by remember { mutableStateOf("") }
    var intolerances by remember { mutableStateOf(listOf<String>()) }
    var diets by remember { mutableStateOf(listOf<String>()) }
    var ingredients by remember { mutableStateOf(listOf<IngredientComponent>()) }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf(listOf<String>()) }
    var selectedImageBytesList by remember { mutableStateOf<List<ByteArray>>(emptyList()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val byteArrayList = uris.mapNotNull { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            selectedImageBytesList = byteArrayList
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    Scaffold(
        topBar = {
            TopBar(
                titleText = "Create a Recipe",
                backButton = true,
                onBackButton = onBackButton,
                enableButtons = buttonsEnable,
                icon = Icons.Filled.Person
            )
        },
        bottomBar = { BottomBar(buttonsEnable = buttonsEnable) },
        containerColor = Color.White,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(20.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Recipe Form",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                FormTextField(
                    parameterName = "Name",
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.padding(horizontal = 30.dp),
                    enabled = buttonsEnable
                )
                FormTextField(
                    parameterName = "Description",
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.padding(horizontal = 30.dp),
                    enabled = buttonsEnable
                )
                NumberLineTextField(
                    parameterName = "Duration (min)",
                    value = preparationTime,
                    onValueChange = { if (isValidForNumberTextField(it)) preparationTime = it },
                    modifier = Modifier.padding(horizontal = 30.dp),
                    enabled = buttonsEnable
                )
                NumberLineTextField(
                    parameterName = "Serving (px)",
                    value = servings,
                    onValueChange = { if (isValidForNumberTextField(it)) servings = it },
                    modifier = Modifier.padding(horizontal = 30.dp),
                    enabled = buttonsEnable
                )
                DropdownMenuComponent(
                    options = MealType.entries.map { it.displayName },
                    value = mealType,
                    onValueChange = { mealType = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Meal Type",
                )
                DropdownMenuComponent(
                    options = Cuisine.entries.map { it.displayName },
                    value = cuisine,
                    onValueChange = { cuisine = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Cuisine",
                )
                MultiSelectDropdownMenuComponent(
                    options = Intolerance.entries.map { it.displayName },
                    values = intolerances,
                    onValuesChange = { intolerances = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Intolerances"
                )
                MultiSelectDropdownMenuComponent(
                    options = Diet.entries.map { it.displayName },
                    values = diets,
                    onValuesChange = { diets = it },
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = buttonsEnable,
                    label = "Diets"
                )
                NutritionalInfoComponent(
                    calories = calories,
                    onCaloriesChange = { if (isValidForNumberTextField(it)) calories = it },
                    protein = protein,
                    onProteinChange = { if (isValidForNumberTextField(it)) protein = it },
                    fat = fat,
                    onFatChange = { if (isValidForNumberTextField(it)) fat = it },
                    carbs = carbs,
                    onCarbsChange = { if (isValidForNumberTextField(it)) carbs = it },
                    enableButtons = buttonsEnable
                )
                DividerComponent()
                IngredientsComponent(
                    ingredients = ingredients,
                    onIngredientsChange = { ingredients = it },
                    enabled = buttonsEnable
                )
                DividerComponent()
                InstructionsComponent(
                    steps = instructions,
                    onStepsChange = { instructions = it },
                    enabled = buttonsEnable
                )
                DividerComponent()
                Button(
                    onClick = {
                        if (galleryPermissionState.status.isGranted) {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else galleryPermissionState.launchPermissionRequest()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    enabled = buttonsEnable
                ) {
                    Text("Upload")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Image, contentDescription = null)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    selectedImageBytesList.forEach { byteArray ->
                        Image(
                            painter = rememberAsyncImagePainter(byteArray),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                        )
                    }
                }
                Button(
                    onClick = {
                        val instructionsSteps = instructions.mapIndexed { index, step ->
                            (index + 1).toString() to step
                        }.toMap()

                        onCreateRecipe(
                            name,
                            description,
                            preparationTime.toInt(),
                            servings.toInt(),
                            Cuisine.valueOf(
                                cuisine.uppercase().replace(Regex("[\\s-]"), "_")
                            ),
                            MealType.valueOf(
                                mealType.uppercase().replace(Regex("[\\s-]"), "_")
                            ),
                            intolerances.map {
                                Intolerance.valueOf(
                                    it.uppercase().replace(Regex("[\\s-]"), "_")
                                )
                            }.toSet(),
                            diets.map { Diet.valueOf(
                                it.uppercase().replace(Regex("[\\s-]"), "_")
                            ) }.toSet(),
                            ingredients.map {
                                Ingredient(
                                    it.name,
                                    it.quantity.toDouble(),
                                    IngredientUnit.fromString(it.unit)
                                )
                            },
                            calories.toIntOrNull(),
                            protein.toIntOrNull(),
                            fat.toIntOrNull(),
                            carbs.toIntOrNull(),
                            Instructions(instructionsSteps),
                            selectedImageBytesList
                        )
                    },
                    modifier = Modifier.padding(10.dp),
                    enabled = buttonsEnable && !name.isEmpty() && !description.isEmpty() &&
                    !preparationTime.isEmpty() && !servings.isEmpty() && !cuisine.isEmpty() &&
                    !mealType.isEmpty() && !ingredients.isEmpty() && !instructions.isEmpty()
                ) {
                    if (buttonsEnable) Text("Create")
                    else LoadingSpinner(Modifier.size(30.dp))
                }
            }
        }
    )
}

@Preview
@Composable
fun CreateRecipeScreenPreview() {
    CreateRecipeScreen({}, { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }, true)
}