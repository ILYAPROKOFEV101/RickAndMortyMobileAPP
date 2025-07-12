package com.ilya.rickandmorty.api

import com.ilya.rickandmorty.data.Character
import com.ilya.rickandmorty.data.CharacterResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): CharacterResponse

    @GET("character/{id}")
    suspend fun getCharacterDetails(@Path("id") id: Int): Character?
}





