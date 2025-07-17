package android.epicurius.ui.screens.utils

import android.epicurius.ui.screens.theme.Beige
import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.theme.Lilac
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(label) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Lilac,
            unfocusedIndicatorColor = Lilac,
            focusedLabelColor = Beige,
            unfocusedLabelColor = Beige,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        )
    )
}

@Composable
fun MixedText(
    boldString: String,
    normalString: String,
    color: Color = Color.Black
) {
    Row {
        Text(text = boldString, color = color, fontWeight = FontWeight.Bold)
        Text(text = normalString, color = color)
    }
}

@Composable
fun SearchTextField(
    text: String,
    modifier: Modifier = Modifier,
    onSearchQueryChange: (query: String) -> Unit,
    onIconClick: () -> Unit,
    textColor: Color = DarkPurple,
    iconColor: Color = DarkPurple,
    labelColor: Color = DarkPurple,
    enableButtons: Boolean
) {
    OutlinedTextField(
        value = text,
        onValueChange = onSearchQueryChange,
        placeholder = { Text(text = "Search", color = labelColor) },
        trailingIcon = {
            IconButton(
                onClick = { onIconClick() },
                enabled = enableButtons
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    tint = iconColor
                )
            }
        },
        singleLine = true,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedIndicatorColor = Lilac,
            unfocusedIndicatorColor = Lilac,
            focusedLabelColor = Beige,
            unfocusedLabelColor = Beige,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTrailingIconColor = Beige,
            unfocusedTrailingIconColor = Beige,
        )
    )
}

@Composable
fun FormTextField(
    parameterName: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(parameterName) },
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Lilac,
            unfocusedIndicatorColor = Lilac,
            focusedLabelColor = Beige,
            unfocusedLabelColor = Beige,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        )
    )
}

@Composable
fun NumberLineTextField(
    parameterName: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(parameterName) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Lilac,
            unfocusedIndicatorColor = Lilac,
            focusedLabelColor = Beige,
            unfocusedLabelColor = Beige,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        )
    )
}

@Composable
fun NumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    label: String? = null,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = label?.let { { Text(text = it, color = Beige) } },
        placeholder = placeholder?.let { { Text(text = it, color = Beige) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Lilac,
            unfocusedIndicatorColor = Lilac,
            focusedLabelColor = Beige,
            unfocusedLabelColor = Beige,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTrailingIconColor = Beige,
            unfocusedTrailingIconColor = Beige,
        )
    )
}

fun isValidForNumberTextField(value: String): Boolean {
    val regex = Regex("^\\d*\\.?\\d*\$")
    return value.isEmpty() || regex.matches(value)
}