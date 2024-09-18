package com.example.cards.GameModel

class Deck {
    private val cards: MutableList<Card> = Suit.entries.flatMap { suit ->
        Rank.entries.map { rank -> Card(suit, rank) }
    }.shuffled().toMutableList()

    fun drawCard(): Card = cards.removeAt(0)
}