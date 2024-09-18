package com.example.cards.GameModel

data class Player(
    val id: String,
    val name: String,
    val hand: MutableList<Card> = mutableListOf(),
    var score: Int = 0,
    var minBid: Int = 7,
    var bid: Int = 0,
    var tricksWon: Int = 0,
    val teamId: Int,
    val trumpSuit: Suit
) {
    fun playCard(card: Card): Boolean {
        // Network call to play a card
        return true // Example: This would return success/failure from the server
    }

    fun placeBid(bid: Int): Boolean {
        // Network call to place a bid
        return true // Example: This would return success/failure from the server
    }
}