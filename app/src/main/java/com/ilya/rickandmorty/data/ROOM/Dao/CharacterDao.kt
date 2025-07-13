package com.ilya.rickandmorty.data.ROOM.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ilya.rickandmorty.data.ROOM.Table.CharacterEntity

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: CharacterEntity) // для одиночной вставки

    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :characterId LIMIT 1")
    suspend fun getCharacterById(characterId: Int): CharacterEntity?

    @Query("DELETE FROM characters")
    suspend fun clearAll()


    @Query("""
    SELECT * FROM characters
    WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
      AND (:status IS NULL OR status = :status)
      AND (:species IS NULL OR species = :species)
      AND (:gender IS NULL OR gender = :gender)
    ORDER BY id ASC
    LIMIT :limit OFFSET :offset
""")
    suspend fun getCharactersWithFilters(
        name: String?,
        status: String?,
        species: String?,
        gender: String?,
        limit: Int,
        offset: Int
    ): List<CharacterEntity>


    @Query("SELECT COUNT(*) FROM characters WHERE (:name IS NULL OR name LIKE '%' || :name || '%') AND (:status IS NULL OR status = :status) AND (:species IS NULL OR species = :species) AND (:gender IS NULL OR gender = :gender)")
    suspend fun getCountWithFilters(
        name: String?,
        status: String?,
        species: String?,
        gender: String?
    ): Int
}
