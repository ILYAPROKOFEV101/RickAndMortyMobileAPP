package com.ilya.rickandmorty.presentation

import com.ilya.rickandmorty.api.RickAndMortyApi
import com.ilya.rickandmorty.data.CharacterResponse

class CharacterRepository(private val api: RickAndMortyApi) {
    suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        species: String?,
        gender: String?
    ): CharacterResponse {
        return api.getCharacters(page, name, status, species, gender)
    }
}
