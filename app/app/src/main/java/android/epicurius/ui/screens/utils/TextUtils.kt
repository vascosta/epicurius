package android.epicurius.ui.screens.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(label) },
        modifier = modifier
    )
}

@Composable
fun MixedText(boldString: String, normalString: String) {
    Row {
        Text(text = boldString, fontWeight = FontWeight.Bold)
        Text(normalString)
    }
}

@Composable
fun SearchTextField(
    text: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun FormTextField(parameterName: String, value: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(parameterName) },
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.Black
        )
    )
}

@Composable
fun NumberTextField(parameterName: String, value: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(parameterName) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.Black
        )
    )
}
