package com.gruppe5.roguelike.property

import kotlinx.serialization.Serializable

@Serializable
data class Buff(
    var duration: Int = 0,
    var statsMod: StatModifier = StatModifier()
)
