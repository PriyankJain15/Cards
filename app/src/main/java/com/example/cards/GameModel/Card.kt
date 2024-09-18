package com.example.cards.GameModel

data class Card(var suit: Suit, var rank: Rank)

enum class Suit{
    SPADES,HEARTS,DIAMONDS,CLUBS
}

enum class Rank(var value:Int) {
    TWO(2), THREE(3),
    FOUR(4), FIVE(5),
    SIX(6), SEVEN(7),
    EIGHT(8), NINE(9),
    TEN(10), JACK(11),
    QUEEN(12), KING(13),
    ACE(14)
}