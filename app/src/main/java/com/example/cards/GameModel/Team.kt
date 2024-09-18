package com.example.cards.GameModel

data class Team(
    val id: Int,
    val players: List<Player>,
    var score: Int = 0,
    var oppscore:Int = 0,
    var minBid:Int= 7,
    var highestBid: Int = 0,
    var totalTricksWon: Int = 0
) {

    // team with highest bid play   x = 6   highestBid = 8
    //    highestBid team won -> if our score is x then new score will be x - highestBid (if x becomes -ve then opponent score is 2)
    //                        -> if opponent score is x the new score will be x + highestBid = 14
    //    highestBid team loss -> if our score is x then new score will be x + (2*highestBid) = 22
    //                        -> if opponent score is x the new score will be x -(2*highestBid) (if x becomes -ve then our score is 10)

    // highest bid alag alag team ke liye update krni hain

    // logic update krna padega ki jis team ki highestBid hain uss hisab se kaam krna padega taaki highestBid team and opponent
    // team ke score sahi show ho kyuki same code dono phone me run hoga or dono me highest bid same hui toh score sahi nhi niklega
    // example

    fun updateScore() {
//        if (totalTricksWon >= highestBid) {
//            score = score - highestBid
//            if (score < 0) {
//                oppscore = oppscore - score
//                score = 0
//            }
//        } else {
//            score = score + (2 * highestBid)
//            oppscore = oppscore - (2 * highestBid)
//            if (oppscore < 0) {
//                oppscore = 0
//            }
//        }
    }
}