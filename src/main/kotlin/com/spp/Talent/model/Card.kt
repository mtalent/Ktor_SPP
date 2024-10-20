package com.spp.Talent.model

import kotlinx.serialization.Serializable


@Serializable
data class Card(val value: String, val suit: String)
