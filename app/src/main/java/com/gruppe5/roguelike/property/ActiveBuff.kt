package com.gruppe5.roguelike.property

// -1 = infinite
data class ActiveBuff(
    val id: String,
    val statsMod: StatModifier,
    var remainingTurns: Int
)
