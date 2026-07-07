package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.Rarity
import kotlinx.serialization.Serializable

/**
 * gleiches :seal: prinzip wie bei enemy
 */
@Serializable
sealed class ItemDefinition {
    abstract val label: String
    abstract val imageResId: Int
    abstract val rarity: Rarity
}
