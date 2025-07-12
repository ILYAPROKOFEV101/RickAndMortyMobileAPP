package com.ilya.rickandmorty.presentation

import com.ilya.rickandmorty.api.RickAndMortyApi
import com.ilya.rickandmorty.data.CharacterResponse
import com.ilya.rickandmorty.data.ROOM.Dao.CharacterDao


import android.util.Log
import com.ilya.rickandmorty.data.PageInfo
import com.ilya.rickandmorty.data.ROOM.toCharacter
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
        try {
            val response = api.getCharacters(page, name, status, species, gender)
            dao.insertAll(response.results.map { it.toEntity() })
            return response
        } catch (e: Exception) {
            Log.e("CharacterRepository", "API failed, loading from DB", e)
            if (page == 1) {
                val cached = dao.getAllCharacters()
                if (cached.isNotEmpty()) {
                    return CharacterResponse(
                        info = PageInfo(
                            count = cached.size,
                            pages = 1,
                            next = null,
                            prev = null
                        ),
                        results = cached.map { it.toCharacter() }
                    )
                }
            }
            throw e
        }
    }
}
