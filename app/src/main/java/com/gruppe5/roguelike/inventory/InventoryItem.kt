package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.inventory.item_types.ItemDefinition
import kotlinx.serialization.Serializable

@Serializable
class ItemInstance(
    val definition: ItemDefinition,
    val usages: Int,
) {
    val label: String get() = definition.label
    val imageResId: Int get() = definition.imageResId
    val rarity: Rarity get() = definition.rarity
}
