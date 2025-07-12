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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.rickandmorty.api.RetrofitClient
import com.ilya.rickandmorty.data.Character
import com.ilya.rickandmorty.data.ROOM.DB.AppDatabase
import com.ilya.rickandmorty.data.ROOM.toCharacter
import com.ilya.rickandmorty.data.ROOM.toEntity
import com.ilya.rickandmorty.ui.theme.formatCreatedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(characterId: Int) {
    val context = LocalContext.current

    val characterDao = remember {
        AppDatabase.getDatabase(context).characterDao()
    }

    val characterState = produceState<Character?>(initialValue = null, characterId) {
        // Сначала пытаемся получить персонажа из БД
        val fromDb = withContext(Dispatchers.IO) {
            characterDao.getCharacterById(characterId)
        }

        if (fromDb != null) {
            value = fromDb.toCharacter() // предполагается, что у Entity есть метод преобразования в Character
        } else {
            // Если в БД нет, грузим из сети
            val fromNetwork = withContext(Dispatchers.IO) {
                RetrofitClient.api.getCharacterDetails(characterId)
            }

            value = fromNetwork

            // Сохраняем в БД асинхронно
            withContext(Dispatchers.IO) {
                fromNetwork?.let { characterDao.insert(it.toEntity()) } // предполагается, что Character -> Entity маппинг есть
            }
        }
    }

    AppTheme {
        Scaffold(
            containerColor = Theme.colors.primaryBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Character Details",
                            color = Theme.colors.textColor
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Theme.colors.primaryBackground
                    )
                )
            }
        ) { padding ->
            characterState.value?.let { char ->
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = character.name,
            style = MaterialTheme.typography.displayMedium,
            color = Theme.colors.textColor,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            DetailItem("Status", character.status)
            DetailItem("Species", character.species)
            DetailItem("Type", if (character.type.isNotEmpty()) character.type else "Unknown")
            DetailItem("Gender", character.gender)
            DetailItem("Origin", character.origin.name)
            DetailItem("Location", character.location.name)

            val episodeNumbers = remember(character.episode) {
                character.episode.map { it.substringAfterLast("/") }
            }

            DetailItem("Episodes", episodeNumbers.joinToString(", "))

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
