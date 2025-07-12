package com.ilya.rickandmorty.compose

import AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ilya.rickandmorty.data.Character as RMCharacter
import com.ilya.rickandmorty.presentation.CharacterViewModel
import com.ilya.rickandmorty.ui.theme.AppTypography

@Composable
fun CharacterListScreen(
    viewModel: CharacterViewModel = viewModel(),
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var filters by remember { mutableStateOf(CharacterFilters()) }

    val characters = viewModel.getCharacters(
        name = if (searchQuery.isNotEmpty()) searchQuery else null,
        status = filters.status,
        species = filters.species,
        gender = filters.gender
    ).collectAsLazyPagingItems()

    AppTheme {
        Scaffold(
            containerColor = Theme.colors.primaryBackground,
            topBar = {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { characters.refresh() },
                    onFilterClick = { showFilters = true }
                )
            }
        ) { padding ->
            CharacterGrid(
                characters = characters,
                padding = padding,
                onItemClick = { character ->
                    navController.navigate("character_detail/${character.id}")
                }
            )
        }

        if (showFilters) {
            FilterDialog(
                filters = filters,
                onApply = {
                    filters = it
                    characters.refresh()
                    showFilters = false
                },
                onDismiss = { showFilters = false }
            )
        }
    }
}


@Composable
fun CharacterGrid(
    characters: androidx.paging.compose.LazyPagingItems<RMCharacter>,
    padding: PaddingValues,
    onItemClick: (RMCharacter) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = padding,
        modifier = Modifier
            .background(Theme.colors.primaryBackground)
    ) {
        items(characters.itemCount) { index ->
            characters[index]?.let { character ->
                CharacterItem(character = character, onClick = onItemClick)
            }
        }

        if (characters.loadState.append == LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Theme.colors.primaryAction)
                }
            }
        }
    }
}


@Composable
fun CharacterItem(character: RMCharacter, onClick: (RMCharacter) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick(character) },
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.googleColors
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = character.name,
                fontWeight = FontWeight.Bold,
                style = AppTypography.bodyLarge,
                color = Theme.colors.textColor,
                modifier = Modifier
                    .padding(8.dp)
            )
            Text(
                text = "${character.gender} | ${character.species}",
                style = AppTypography.bodyMedium,
                color = Theme.colors.textColor,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

