package android.epicurius.ui.screens.recipe.profile

import android.annotation.SuppressLint
import android.epicurius.R
import android.epicurius.domain.Diet
import android.epicurius.domain.Intolerance
import android.epicurius.domain.recipe.Cuisine
import android.epicurius.domain.recipe.Ingredient
import android.epicurius.domain.recipe.IngredientUnit
import android.epicurius.domain.recipe.Instructions
import android.epicurius.domain.recipe.MealType
import android.epicurius.domain.recipe.Recipe
import android.epicurius.ui.screens.BottomBar
import android.epicurius.ui.screens.TopBar
import android.epicurius.ui.screens.utils.MixedText
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeProfileScreen(recipe: Recipe, rating: Double, images: List<Int>, isAuthor: Boolean) {
    Scaffold(
        topBar = { TopBar(text = recipe.name, backButton = true) },
        bottomBar = { BottomBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row{
                    Text("$rating/5")

                    Spacer(Modifier.size(5.dp))

                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Favorites",
                        modifier = Modifier
                            .padding(top = (0.5).dp)
                            .size(15.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                IconButton(onClick = { }) {
                    Image(
                        painter = painterResource(id = R.drawable.white_star),
                        contentDescription = "Favorites",
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            val pagerState = rememberPagerState(pageCount = { images.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .height(250.dp)
            ) { page ->
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            HorizontalPagerIndicator(images.size, pagerState)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.End
            ) { MixedText("by ", recipe.authorUsername) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.description,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 10.dp, end = 10.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.CenterStart
            ) {

                if (isAuthor) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 5.dp, end = 10.dp)
                    ) {
                        Text("Edit")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    MixedText("Servings: ", "${recipe.servings} px")
                    MixedText("Preparation Time: ", "${recipe.preparationTime} min")
                    MixedText("Meal Type: ", recipe.mealType.displayName)
                    MixedText("Cuisine: ", recipe.cuisine.displayName)
                    MixedText("Intolerances: ", recipe.intolerances.joinToString(", ") { it.displayName })
                    MixedText("Diets: ", recipe.diets.joinToString(", ") { it.displayName })
                    MixedText("Calories: ", recipe.calories?.toString() ?: "N/A")
                    MixedText("Protein: ", recipe.protein?.toString() ?: "N/A")
                    MixedText("Fat: ", recipe.fat?.toString() ?: "N/A")
                    MixedText("Carbs: ", recipe.carbs?.toString() ?: "N/A")

                    val ingredients = recipe.ingredients.joinToString("\n") {
                        val formattedQuantity = if (it.quantity % 1.0 == 0.0) {
                            it.quantity.toInt()
                        } else {
                            it.quantity
                        }

                        val formattedUnit =
                            when(it.unit) {
                                IngredientUnit.G -> "g"
                                IngredientUnit.ML -> "ml"
                                IngredientUnit.X -> ""
                                IngredientUnit.TSP -> "tsp"
                                IngredientUnit.L -> "l"
                                IngredientUnit.Kg -> "Kg"
                                IngredientUnit.CUPS -> "cups"
                                IngredientUnit.TBSP -> "tbsp"
                                IngredientUnit.DSP -> "dsp"
                                IngredientUnit.TEA_CUP -> "Tea cup"
                                IngredientUnit.COFFEE_CUP -> "Coffee cup"
                            }

                        "$formattedQuantity$formattedUnit ${it.name}"
                    }
                    Text("Ingredients:", fontWeight = FontWeight.Bold)
                    Text(text = ingredients, modifier = Modifier.padding(start = 10.dp))

                    val instructions = recipe.instructions.steps.entries.joinToString("\n") { "${it.key}: ${it.value}" }
                    Text("Instructions:", fontWeight = FontWeight.Bold)
                    Text(text = instructions, modifier = Modifier.padding(start = 10.dp))
                }
            }

            Row {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(top = 5.dp, end = 10.dp),
                ) {
                    Text("Make it!")
                }
            }
        }
    }
}

@Composable
private fun HorizontalPagerIndicator(size: Int, pagerState: PagerState) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(size) { index ->
            val color = if (pagerState.currentPage == index) Color.Blue else Color.Gray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun RecipeProfilePreview(){
    val recipe = Recipe(
        id = 1,
        name = "Panquecas Americanas",
        authorUsername = "MestreAndre",
        date = LocalDate.of(2025, 5, 19),
        description = "Deliciosas panquecas fofinhas perfeitas para o pequeno-almoço.",
        servings = 4,
        preparationTime = 20,
        cuisine = Cuisine.AMERICAN,
        mealType = MealType.BREAKFAST,
        intolerances = listOf(Intolerance.GLUTEN),
        diets = listOf(Diet.VEGETARIAN),
        ingredients = listOf(
            Ingredient("Farinha de trigo", 200.0, IngredientUnit.G),
            Ingredient("Leite", 300.0, IngredientUnit.ML),
            Ingredient("Ovo", 2.0, IngredientUnit.X),
            Ingredient("Açúcar", 50.0, IngredientUnit.G),
            Ingredient("Fermento em pó", 10.0, IngredientUnit.G),
            Ingredient("Sal", 1.0, IngredientUnit.TSP),
            Ingredient("Manteiga", 30.0, IngredientUnit.G)
        ),
        calories = 350,
        protein = 8,
        fat = 10,
        carbs = 55,
        instructions = Instructions(
            steps = mapOf(
                "1" to "Numa taça, mistura a farinha, o açúcar, o fermento e o sal.",
                "2" to "Adiciona o leite, os ovos e a manteiga derretida. Mistura até ficar homogéneo.",
                "3" to "Aquece uma frigideira antiaderente e coloca uma concha da massa.",
                "4" to "Cozinha até formar bolhas na superfície e vira a panqueca. Cozinha o outro lado.",
                "5" to "Serve quente com xarope de ácer ou frutas."
            )
        ),
        pictures = listOf()
    )
    val rating = 4.0
    RecipeProfileScreen(recipe, rating, listOf(R.drawable.home, R.drawable.star, R.drawable.pencil), true)
}