package com.ilya.rickandmorty.compose

import AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.rickandmorty.api.RetrofitClient
import com.ilya.rickandmorty.data.Character
import com.ilya.rickandmorty.ui.theme.formatCreatedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(characterId: Int) {
    val character by produceState<Character?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            RetrofitClient.api.getCharacterDetails(characterId)
        }
    }

    AppTheme { // ✅ оборачиваем всё в кастомную тему
        Scaffold(
            containerColor = Theme.colors.primaryBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Character Details",
                            color = Theme.colors.textColor // ✅ цвет текста
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Theme.colors.primaryBackground
                    )
                )
            }
        ) { padding ->
            character?.let { char ->
                CharacterDetailsContent(
                    character = char,
                    modifier = Modifier.padding(padding)
                )
            } ?: run {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Theme.colors.primaryBackground),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Theme.colors.primaryAction)
                }
            }
        }
    }
}

@Composable
fun CharacterDetailsContent(character: Character, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .background(Theme.colors.primaryBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Центрирует всё внутри
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            modifier = Modifier
                .size(200.dp)
        )

        // Отцентрированное имя под фотографией
        Text(
            text = character.name,
            style = MaterialTheme.typography.displayMedium, // Используем ваш шрифт
            color = Theme.colors.textColor,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // Остальные детали идут по левому краю
        Column(modifier = Modifier.fillMaxWidth()) {
            DetailItem("Status", character.status)
            DetailItem("Species", character.species)
            DetailItem("Type", if (character.type.isNotEmpty()) character.type else "Unknown")
            DetailItem("Gender", character.gender)
            DetailItem("Origin", character.origin.name)
            DetailItem("Location", character.location.name)
            DetailItem("Episodes", character.episode.size.toString())
            DetailItem("Created", character.created.formatCreatedDate())
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            color = Theme.colors.textColor
        )
        Text(
            text = value,
            color = Theme.colors.textColor
        )
    }
}
