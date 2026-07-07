package com.gruppe5.roguelike.property

import kotlinx.serialization.Serializable

/**
 * Für time-based sachn
 */
@Serializable
data class Effect(
    val label: String,
    val imageResId: Int,
    val statsMod: StatModifier,
    var remaining: Int,
)
