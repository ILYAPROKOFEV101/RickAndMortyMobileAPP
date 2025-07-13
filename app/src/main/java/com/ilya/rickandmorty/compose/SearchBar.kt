package com.ilya.rickandmorty.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import com.ilya.rickandmorty.ui.theme.AppTypography


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Search characters...",
                    style = AppTypography.bodyMedium,
                    color = Theme.colors.textColor.copy(alpha = 0.6f)
                )
            },
            textStyle = AppTypography.bodyMedium.copy(color = Theme.colors.textColor),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Theme.colors.googleColors,
                focusedContainerColor = Theme.colors.googleColors,
                unfocusedIndicatorColor = Theme.colors.primaryAction,
                focusedIndicatorColor = Theme.colors.primaryAction,
                cursorColor = Theme.colors.primaryAction
            ),
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onFilterClick) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filters",
                tint = Theme.colors.primaryAction
            )
        }
    }
}


@Composable
fun FilterDialog(
    filters: CharacterFilters,
    onApply: (CharacterFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var localFilters by remember { mutableStateOf(filters) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Theme.colors.primaryBackground,
        titleContentColor = Theme.colors.textColor,
        textContentColor = Theme.colors.textColor,
        title = {
            Text(
                text = "Filter Characters",
                style = AppTypography.displayMedium
            )
        },
        text = {
            Column {
                FilterField("Status", listOf("Alive", "Dead", "unknown"), localFilters.status) {
                    localFilters = localFilters.copy(status = it)
                }
                FilterField("Species", listOf("Human", "Alien"), localFilters.species) {
                    localFilters = localFilters.copy(species = it)
                }
                FilterField("Gender", listOf("Male", "Female", "Genderless", "unknown"), localFilters.gender) {
                    localFilters = localFilters.copy(gender = it)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(localFilters) },
                colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primaryAction)
            ) {
                Text("Apply", color = Theme.colors.textColor, style = AppTypography.bodyMedium)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.googleColors)
            ) {
                Text("Cancel", color = Theme.colors.textColor, style = AppTypography.bodyMedium)
            }
        }
    )
}



@Composable
fun FilterField(
    title: String,
    options: List<String>,
    selected: String?,
    onSelectionChange: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Theme.colors.googleColors,
                contentColor = Theme.colors.textColor
            )
        ) {
            Text(
                "$title: ${selected ?: "Any"}",
                style = AppTypography.bodyMedium,
                color = Theme.colors.textColor
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Theme.colors.textColor
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Theme.colors.googleColors)
        ) {
            DropdownMenuItem(
                text = {
                    Text("Any", style = AppTypography.bodyMedium, color = Theme.colors.textColor)
                },
                onClick = {
                    onSelectionChange(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option, style = AppTypography.bodyMedium, color = Theme.colors.textColor)
                    },
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    } //
                )
            }
        }
    }
}



data class CharacterFilters(
    val status: String? = null,
    val species: String? = null,
    val gender: String? = null
)