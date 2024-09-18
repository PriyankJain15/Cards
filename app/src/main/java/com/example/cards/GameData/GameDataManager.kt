package com.example.cards.GameData

import android.content.Context
import com.google.gson.Gson

class GameDataManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("spades_game", Context.MODE_PRIVATE)

    fun saveGameState(gameState: GameState) {
        sharedPreferences.edit()
            .putString("game_state", Gson().toJson(gameState))
            .apply()
    }

    fun loadGameState(): GameState? {
        val json = sharedPreferences.getString("game_state", null)
        return if (json != null) Gson().fromJson(json, GameState::class.java) else null
    }

    fun syncWithServer(gameId: String): GameState? {
        // Logic to sync with the server, retrieving the latest game state
        return null // Placeholder
    }
}