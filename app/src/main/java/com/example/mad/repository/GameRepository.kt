package com.example.mad.repository

import android.content.Context
import com.example.mad.data.Game
import com.example.mad.db.GameDao
import com.example.mad.db.GameRoomDatabase

class GameRepository(context: Context) {

    private val gameDao: GameDao

    init {
        val database = GameRoomDatabase.getDatabase(context)
        gameDao = database!!.gameDao()
    }

    suspend fun insert(game: Game) = gameDao.insert(game)

    suspend fun delete(game: Game) = gameDao.delete(game)

    fun getGames() = gameDao.getGames()

    suspend fun deleteAll() = gameDao.deleteAll()

}
