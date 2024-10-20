package com.spp.Talent.evaluate

import com.spp.Talent.model.Card

fun evaluateHand(hand: List<Card>): Int {
    // First, sort the hand by the rank of the cards
    val sortedHand = hand.sortedBy { cardRank(it.value) }

    val isFlush = hand.all { it.suit == hand[0].suit }
    val isStraight = isStraight(sortedHand.map { cardRank(it.value) })

    val rankCounts = hand.groupingBy { it.value }.eachCount()  // Map of card value to count

    return when {
        // Check for Royal Flush
        isStraight && isFlush && sortedHand.last().value == "A" -> 10  // Royal Flush
        // Check for Straight Flush
        isStraight && isFlush -> 9  // Straight Flush
        // Four of a Kind
        4 in rankCounts.values -> 8  // Four of a Kind
        // Full House
        3 in rankCounts.values && 2 in rankCounts.values -> 7  // Full House
        // Flush
        isFlush -> 6  // Flush
        // Straight
        isStraight -> 5  // Straight
        // Three of a Kind
        3 in rankCounts.values -> 4  // Three of a Kind
        // Two Pair
        rankCounts.values.count { it == 2 } == 2 -> 3  // Two Pair
        // One Pair
        2 in rankCounts.values -> 2  // One Pair
        // High Card
        else -> 1  // High Card
    }
}

// Helper functions:

// Convert card value to rank (Ace = 14, King = 13, etc.)
fun cardRank(value: String): Int {
    return when (value) {
        "Ace" -> 14
        "King" -> 13
        "Queen" -> 12
        "Jack" -> 11
        else -> value.toInt()  // Convert number cards (2-10) to integer values
    }
}

// Check if the hand is a straight (consecutive card ranks)
fun isStraight(ranks: List<Int>): Boolean {
    return ranks.zipWithNext().all { (a, b) -> b == a + 1 }
}
