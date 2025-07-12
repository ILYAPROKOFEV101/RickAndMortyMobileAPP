package com.ilya.rickandmorty.presentation

import com.ilya.rickandmorty.api.RickAndMortyApi
import com.ilya.rickandmorty.data.CharacterResponse
import com.ilya.rickandmorty.data.ROOM.Dao.CharacterDao


import com.ilya.rickandmorty.data.Character
import com.ilya.rickandmorty.data.ROOM.Table.CharacterEntity
import com.ilya.rickandmorty.data.ROOM.toEntity

class CharacterRepository(
    private val api: RickAndMortyApi,
    private val dao: CharacterDao
) {
    suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        species: String?,
        gender: String?
    ): CharacterResponse {
        return api.getCharacters(page, name, status, species, gender)
    }

    suspend fun cacheCharacters(characters: List<Character>) {
        val entities = characters.map { it.toEntity() } // <- тебе нужно будет сделать toEntity()
        dao.insertAll(entities)
    }

    suspend fun getCharactersFromDb(
        name: String?,
        status: String?,
        species: String?,
        gender: String?,
        limit: Int,
        offset: Int
    ): List<CharacterEntity> {
        return dao.getCharactersWithFilters(name, status, species, gender, limit, offset)
    }

    suspend fun getCountFromDb(
        name: String?,
        status: String?,
        species: String?,
        gender: String?
    ): Int {
        return dao.getCountWithFilters(name, status, species, gender)
    }
}