package com.ilya.rickandmorty.compose

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
import com.ilya.rickandmorty.data.Character // ✅ ВАЖНО!
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("Character Details") }) }
    ) { padding ->
        character?.let { char ->
            CharacterDetailsContent(char, Modifier.padding(padding))
        } ?: run {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CharacterDetailsContent(character: Character, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        DetailItem("Name", character.name)
        DetailItem("Status", character.status)
        DetailItem("Species", character.species)
        DetailItem("Type", if (character.type.isNotEmpty()) character.type else "Unknown") // ✅ исправлено
        DetailItem("Gender", character.gender)
        DetailItem("Origin", character.origin.name)
        DetailItem("Location", character.location.name)
        DetailItem("Episodes", character.episode.size.toString())
        DetailItem("Created", character.created)
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontWeight = FontWeight.Bold)
        Text(value)
    }
}
