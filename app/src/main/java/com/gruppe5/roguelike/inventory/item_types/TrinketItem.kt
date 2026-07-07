package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.Rarity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("trinket")
class TrinketItem(
    override val label: String,
    override val imageResId: Int,
    override val rarity: Rarity = Rarity.COMMON,
) : ItemDefinition()
