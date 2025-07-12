package com.ilya.rickandmorty.presentation

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.rickandmorty.data.Character

class CharacterPagingSource(
    private val repository: CharacterRepository,
    private val name: String?,
    private val status: String?,
    private val species: String?,
    private val gender: String?
) : PagingSource<Int, com.ilya.rickandmorty.data.Character>() {

    override fun getRefreshKey(state: PagingState<Int, com.ilya.rickandmorty.data.Character>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, com.ilya.rickandmorty.data.Character> {
        return try {
            val page = params.key ?: 1
            val response = repository.getCharacters(page, name, status, species, gender)

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.info.next != null) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
