package com.ilya.rickandmorty.compose

import AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ilya.rickandmorty.data.Character as RMCharacter
import com.ilya.rickandmorty.presentation.CharacterViewModel
import com.ilya.rickandmorty.ui.theme.AppTypography
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ilya.rickandmorty.data.hasInternetAccess

@Composable
fun CharacterListScreen(
    viewModel: CharacterViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var filters by remember { mutableStateOf(CharacterFilters()) }
    val gridState = rememberLazyGridState()

    // Получаем поток данных персонажей
    val charactersPagingItems = viewModel.charactersFlow.collectAsLazyPagingItems()

    // Состояние для SwipeRefresh
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = charactersPagingItems.loadState.refresh is LoadState.Loading
    )

    LaunchedEffect(Unit) {
        viewModel.initRepository(context)
        isOnline = hasInternetAccess()
        viewModel.loadCharacters(
            name = if (searchQuery.isNotEmpty()) searchQuery else null,
            status = filters.status,
            species = filters.species,
            gender = filters.gender,
            isOnline = isOnline
        )
    }

    // Обновление данных при изменении параметров
    LaunchedEffect(searchQuery, filters) {
        viewModel.loadCharacters(
            name = if (searchQuery.isNotEmpty()) searchQuery else null,
            status = filters.status,
            species = filters.species,
            gender = filters.gender,
            isOnline = isOnline
        )
    }

    AppTheme {
        Scaffold(
            containerColor = Theme.colors.primaryBackground,
            topBar = {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { charactersPagingItems.refresh() },
                    onFilterClick = { showFilters = true }
                )
            }
        ) { padding ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { charactersPagingItems.refresh() }
            ) {
                CharacterGrid(
                    characters = charactersPagingItems,
                    padding = padding,
                    gridState = gridState,
                    onItemClick = { character ->
                        navController.navigate("character_detail/${character.id}")
                    }
                )
            }
        }

        if (showFilters) {
            FilterDialog(
                filters = filters,
                onApply = {
                    filters = it
                    charactersPagingItems.refresh()
                    showFilters = false
                },
                onDismiss = { showFilters = false }
            )
        }
    }
}


@Composable
fun CharacterGrid(
    characters: LazyPagingItems<RMCharacter>,
    padding: PaddingValues,
    gridState: LazyGridState,
    onItemClick: (RMCharacter) -> Unit
) {
    // Проверяем состояние загрузки и наличие данных
    val isLoading = characters.loadState.refresh is LoadState.Loading
    val isError = characters.loadState.refresh is LoadState.Error
    val isEmpty = characters.loadState.refresh is LoadState.NotLoading && characters.itemCount == 0

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Показываем индикатор загрузки при первой загрузке
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Theme.colors.primaryAction
            )
        } else if (isError) {
            // Показываем сообщение об ошибке
            val error = (characters.loadState.refresh as LoadState.Error).error
            Text(
                text = "Error: ${error.localizedMessage ?: "Unknown error"}",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (isEmpty) {
            // Показываем сообщение, что ничего не найдено
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SearchOff,
                    contentDescription = "No results",
                    tint = Theme.colors.textColor.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No characters found",
                    style = AppTypography.bodyLarge,
                    color = Theme.colors.textColor.copy(alpha = 0.7f)
                )
                if (characters.loadState.refresh is LoadState.NotLoading) {
                    Text(
                        text = "Try different filters",
                        style = AppTypography.bodyMedium,
                        color = Theme.colors.textColor.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 8.dp))
                }
            }
        } else {
            // Показываем список персонажей
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                contentPadding = padding
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

                // Обработка ошибки при подгрузке
                if (characters.loadState.append is LoadState.Error) {
                    item {
                        Text(
                            text = "Error loading more",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                    }
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
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when (character.status.lowercase()) {
                                "alive" -> Color.Green
                                "dead" -> Color.Red
                                else -> Color.Gray
                            }
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = character.status,
                    style = AppTypography.bodyMedium,
                    color = Theme.colors.textColor
                )
            }

            Text(
                text = character.name,
                fontWeight = FontWeight.Bold,
                style = AppTypography.bodyLarge,
                color = Theme.colors.textColor,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "${character.gender} | ${character.species}",
                style = AppTypography.bodyMedium,
                color = Theme.colors.textColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
