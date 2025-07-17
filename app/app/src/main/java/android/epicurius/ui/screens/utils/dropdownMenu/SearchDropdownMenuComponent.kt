package android.epicurius.ui.screens.utils.dropdownMenu

import android.epicurius.ui.screens.theme.DarkPurple
import android.epicurius.ui.screens.utils.LoadState
import android.epicurius.ui.screens.utils.LoadStateRenderer
import android.epicurius.ui.screens.utils.Loaded
import android.epicurius.ui.screens.utils.SearchTextField
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDropdownMenuComponent(
    optionsState: LoadState<List<String>>,
    value: String,
    onValueChange: (String) -> Unit,
    onIconClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color,
    iconColor: Color,
    labelColor: Color,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(),
    ) {
        SearchTextField(
            text = value,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            onSearchQueryChange = onValueChange,
            onIconClick = {
                expanded = true
                onIconClick(value)
            },
            textColor = textColor,
            iconColor = iconColor,
            labelColor = labelColor,
            enableButtons = enabled,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LoadStateRenderer(
                loadState = optionsState,
                content = { options ->
                    if (options.isNotEmpty()) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onValueChange(option)
                                    expanded = false
                                },
                                enabled = enabled
                            )
                        }
                    }
                    else if (optionsState is Loaded) {
                        Text(
                            text = "No valid products found.",
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            )
        }
    }
}
