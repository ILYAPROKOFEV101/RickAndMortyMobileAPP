package com.ilya.rickandmorty.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage

@Composable
fun NavSetup() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "character_list") {
        composable("character_list") {
            CharacterListScreen(navController = navController)
        }
        composable("character_detail/{id}") { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (characterId != null) {
                CharacterDetailScreen(characterId = characterId, navController = navController)
            }
        }

    }
}