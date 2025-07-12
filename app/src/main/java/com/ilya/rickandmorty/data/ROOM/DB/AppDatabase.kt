package com.ilya.rickandmorty.data.ROOM.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ilya.rickandmorty.data.ROOM.Dao.CharacterDao
import com.ilya.rickandmorty.data.ROOM.Table.CharacterEntity
import com.ilya.rickandmorty.data.ROOM.Converters


@Database(entities = [CharacterEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "character_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
