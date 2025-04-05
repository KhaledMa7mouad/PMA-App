package com.example.pmaapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Upsert
    suspend fun upsertPlayer(player: Player)

    @Query("SELECT * FROM player")
    fun getAllPlayers(): Flow<List<Player>>

    @Delete
    suspend fun deletePlayer(player: Player)
}
