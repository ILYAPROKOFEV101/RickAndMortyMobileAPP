package com.ilya.rickandmorty.presentation

import androidx.paging.PagingState
import com.ilya.rickandmorty.api.RetrofitClient
import com.ilya.rickandmorty.api.RickAndMortyApi
import com.ilya.rickandmorty.data.CharacterResponse


import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.ilya.rickandmorty.data.Character
import kotlinx.coroutines.flow.Flow

class CharacterViewModel : ViewModel() {
    private val repository = CharacterRepository(RetrofitClient.api)

    fun getCharacters(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null
    ): Flow<PagingData<com.ilya.rickandmorty.data.Character>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                CharacterPagingSource(repository, name, status, species, gender)
            }
        ).flow
    }
}


