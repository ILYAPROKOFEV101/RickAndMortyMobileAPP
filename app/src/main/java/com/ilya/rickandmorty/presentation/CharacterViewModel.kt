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


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ilya.rickandmorty.data.ROOM.DB.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class CharacterViewModel : ViewModel() {


    private lateinit var repository: CharacterRepository

    fun initRepository(context: Context) {
        if (!::repository.isInitialized) {
            val dao = AppDatabase.getDatabase(context).characterDao()
            repository = CharacterRepository(RetrofitClient.api, dao)
        }
    }

    private val _charactersFlow = MutableStateFlow<PagingData<Character>>(PagingData.empty())
    val charactersFlow = _charactersFlow.asStateFlow()

    fun loadCharacters(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null,
        isOnline: Boolean
    ) {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    CharacterPagingSource(repository, name, status, species, gender, isOnline)
                }
            ).flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _charactersFlow.value = pagingData
                }
        }
    }
}

