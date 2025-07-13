package com.ilya.rickandmorty.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.rickandmorty.data.Character
import com.ilya.rickandmorty.data.ROOM.toCharacter
import retrofit2.HttpException

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

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        Log.d("PagingDebug", "Loading page: $page, isOnline=$isOnline")

        return try {
            if (isOnline) {
                try {
                    val response = repository.getCharacters(page, name, status, species, gender)
                    val characters = response.results

                    // Кешируем в БД
                    repository.cacheCharacters(characters)

                    LoadResult.Page(
                        data = characters,
                        prevKey = if (response.info.prev != null) page - 1 else null,
                        nextKey = if (response.info.next != null) page + 1 else null
                    )
                } catch (e: HttpException) {
                    // Обработка 404 ошибки (ничего не найдено)
                    if (e.code() == 404) {
                        LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    } else {
                        throw e
                    }
                }
            } else {
                // Офлайн логика без изменений
                val pageSize = params.loadSize
                val offset = (page - 1) * pageSize
                val entities = repository.getCharactersFromDb(name, status, species, gender, pageSize, offset)
                val characters = entities.map { it.toCharacter() }
                val totalCount = repository.getCountFromDb(name, status, species, gender)

                val nextPage = if ((page * pageSize) < totalCount) page + 1 else null
                val prevPage = if (page > 1) page - 1 else null

                LoadResult.Page(
                    data = characters,
                    prevKey = prevPage,
                    nextKey = nextPage
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
