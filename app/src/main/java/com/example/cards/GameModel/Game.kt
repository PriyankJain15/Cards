package com.example.cards.GameModel

class Game(val gameId: String, val teams: List<Team>) {
    private val deck = Deck()
    private var currentPlayerIndex: Int = 0
    private val currentTrick: MutableList<Pair<Player, Card>> = mutableListOf()
    var trumpSuit: Suit? = null


    fun dealCards() {
        teams.flatMap { it.players }.forEach { player ->
            repeat(13) { player.hand.add(deck.drawCard()) }
        }
        // Sync this state with the server
    }

    fun chooseTrumpSuit(): Suit {
        // Find the player with the highest bid
        val highestBidPlayer = teams.flatMap { it.players }.maxByOrNull { it.bid }
            ?: throw IllegalStateException("No players found")

        // Logic to ask the highest bidding player to choose a trump suit
        // For simplicity, we'll simulate this by randomly choosing a suit
        // In a real game, you would present a UI to the player to make the choice
        val chosenTrumpSuit = Suit.entries.toTypedArray().random()

        // Store the chosen trump suit in the game state
        this.trumpSuit = chosenTrumpSuit

        // Return the chosen suit
        return chosenTrumpSuit
    }

    fun nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % teams.flatMap { it.players }.size
    }

    fun playCard(player: Player, card: Card) {
        if (teams.flatMap { it.players }[currentPlayerIndex] == player) {
            currentTrick.add(player to card)
            nextPlayer()
            // Send move to the server and update game state
        }
    }

    fun determineTrickWinner(): Player {
        // The lead suit is the suit of the first card played in the trick
        val leadSuit = currentTrick.first().second.suit

        // Determine the winning card considering the trump suit
        val winningPlay = currentTrick.maxWithOrNull(compareBy(
            { it.second.suit == trumpSuit },  // Prefer the trump suit chosen by the highest bidder
            { it.second.suit == leadSuit },   // Within other suits, prefer the lead suit
            { it.second.rank.value }          // Compare rank values within the preferred suits
        )) ?: throw IllegalStateException("No cards were played in this trick")

        // Return the player who played the winning card
        return winningPlay.first
    }

    fun scoreRound() {
        teams.forEach { team ->
            team.totalTricksWon = team.players.sumOf { it.tricksWon }
//            team.highestBid = team.players.sumOf { it.bid }   Change this method with highest bid choosen among player
            team.updateScore()
        }
        // Update and sync this state with the server
    }

    fun isGameOver(): Boolean {
        return teams.any { it.score >= 52 }
    }

    fun getWinningTeam(): Team? {
        return if (isGameOver()) teams.minByOrNull { it.score } else null
    }
}