package com.example.cards.GameData

import com.example.cards.GameModel.Card

data class GameState(
    val teams: List<TeamState>,
    val currentPlayerIndex: Int,
    val deck: List<Card>,
    val currentTrick: List<Pair<String, Card>>, // player ID and card
    val lastMove: Pair<String, Card>? = null, // player ID and last played card
    val gamePhase: GamePhase
)

data class TeamState(
    val id: Int,
    val players: List<PlayerState>,
    val score: Int,
    val totalBids: Int,
    val totalTricksWon: Int
)

data class PlayerState(
    val id: String,
    val name: String,
    val hand: List<Card>,
    val score: Int,
    val bid: Int,
    val tricksWon: Int
)

enum class GamePhase {
    BIDDING, PLAYING, SCORING
}