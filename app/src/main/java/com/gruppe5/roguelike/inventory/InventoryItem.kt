package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.inventory.item_types.ItemDefinition
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff
import kotlinx.serialization.Serializable

@Serializable
class ItemInstance(
    val definition: ItemDefinition,
    val usages: Int,
) {
    val label: String get() = definition.label
    val imageResId: Int get() = definition.imageResId
    val isPermanent: Boolean get() = definition.isPermanent

    fun use(player: Player): List<ActiveBuff> = definition.onUse(this, player) //gibt Buffs zruck
}
