package com.ilya.rickandmorty.presentation

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.rickandmorty.data.Character

class CharacterPagingSource(
    private val repository: CharacterRepository,
    private val name: String?,
    private val status: String?,
    private val species: String?,
    private val gender: String?
) : PagingSource<Int, Character>() {

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        Log.d("CharacterPaging", "getRefreshKey called")
        return state.anchorPosition?.let { position ->
            val key = state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
            Log.d("CharacterPaging", "Refresh key calculated: $key")
            key
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        Log.d("CharacterPaging", "Loading page: $page with filters - name=$name, status=$status, species=$species, gender=$gender")

        return try {
            val response = repository.getCharacters(page, name, status, species, gender)
            Log.d("CharacterPaging", "Loaded ${response.results.size} characters, next = ${response.info.next}")

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.info.next != null) page + 1 else null
            )
        } catch (e: Exception) {
            Log.e("CharacterPaging", "Error loading characters", e)
            LoadResult.Error(e)
        }
    }
}
