package com.example.cards

data class GameModelFirebase(
    val gameCode: String = "",
    var status: Status? = null,
    var teams: MutableList<Team> = mutableListOf(),
    val currentTrick: Trick? = null,
    val highestBidSuit: Suit? = null,
    val highestBid: Int = 0,
    var highestBidPlayer: Team.Player? = null,
    var roomCreatorId:String = "",
    var roomId:String = ""
) {

    data class Team(
        var players: MutableList<Player> = mutableListOf<Player>(),
        var score: Int = 0,
        var oppscore:Int = 0,
        var highestBid: Int = 0,
        var totalTricksWon: Int = 0,
        val teamId: Int?=null,
        var highestBidPlayer: Player? = null
    ) {
        data class Player(
            val name: String = "",
            val profileImageResId: Int = R.drawable.profile10,
            val id: String = "",
            val hand: MutableList<Card> = mutableListOf(),
            var bid: Int = 0,
            var tricksWon: Int = 0,
            val teamId: Int?= null,
            val trumpSuit:Suit = Suit.SPADES
        )
    }

    data class Trick(
        val cards: List<Card> = listOf(),
        val winner: String? = null
    )

    data class Card(
        val rank: Rank,
        val suit: Suit
    )

    enum class Rank(var value:Int) { TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
        EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13), ACE(14)
    }
    enum class Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES
    }
    enum class Status{
        WAITING,PLAYING,SCORING,DELETED
    }
}
