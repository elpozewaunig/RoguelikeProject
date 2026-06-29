package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.ItemDefinition
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.property.ActiveBuff
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Buff

class StatBuffItem(
    label: String,
    imageResId: Int,
    val buff: Buff,
    isPermanent: Boolean = false
) : ItemDefinition(label, imageResId, isPermanent) {

    override fun onUse(instance: ItemInstance, player: Player): List<ActiveBuff> {
        val id = "buff-${System.identityHashCode(instance)}-${System.currentTimeMillis()}"
        val active = ActiveBuff(id = id, statsMod = buff.statsMod, remainingTurns = buff.duration)
        return listOf(active)
    }
}
