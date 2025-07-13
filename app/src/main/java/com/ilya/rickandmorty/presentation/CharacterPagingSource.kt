package com.ilya.rickandmorty.presentation

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.rickandmorty.data.Character
import com.ilya.rickandmorty.data.ROOM.toCharacter

class CharacterPagingSource(
    private val repository: CharacterRepository,
    private val name: String?,
    private val status: String?,
    private val species: String?,
    private val gender: String?,
    private val isOnline: Boolean
) : PagingSource<Int, Character>() {

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        Log.d("PagingDebug", "Loading page: $page, isOnline=$isOnline")

        val pageSize = params.loadSize

        return try {
            val characters: List<Character>
            val totalCount: Int

            if (isOnline) {
                // Получаем с сервера
                val response = repository.getCharacters(page, name, status, species, gender)
                characters = response.results
                totalCount = response.info.count

                // Кешируем в БД
                repository.cacheCharacters(characters)
            } else {
                // Получаем из локальной базы с фильтрами и пагинацией
                val offset = (page - 1) * pageSize
                val entities = repository.getCharactersFromDb(name, status, species, gender, pageSize, offset)
                characters = entities.map { it.toCharacter() }
                totalCount = repository.getCountFromDb(name, status, species, gender)
            }

            val nextKey = if ((page * pageSize) >= totalCount) null else page + 1
            val prevKey = if (page == 1) null else page - 1

            LoadResult.Page(
                data = characters,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
