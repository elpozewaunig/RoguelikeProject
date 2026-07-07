package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff
import kotlinx.serialization.Serializable

@Serializable
sealed class ItemDefinition {
    abstract val label: String
    abstract val imageResId: Int
    abstract val isPermanent: Boolean
    abstract fun onUse(instance: ItemInstance, player: Player): List<ActiveBuff>
}
