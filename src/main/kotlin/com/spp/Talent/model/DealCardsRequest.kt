package com.spp.Talent.model

data class DealCardsRequest(val gameID: Int, val playerId: Int, val cards: List<String>)
