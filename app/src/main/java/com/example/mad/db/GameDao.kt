package com.example.mad.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mad.data.Game

@Dao
interface GameDao {

    @Query("SELECT * from game ORDER BY `release` ASC")
    fun getGames(): LiveData<List<Game>>

    @Insert
    suspend fun insert(game: Game)

    @Insert
    suspend fun insert(game: List<Game>)

    @Delete
    suspend fun delete(game: Game)

    @Query("DELETE from game")
    suspend fun deleteAll()

}