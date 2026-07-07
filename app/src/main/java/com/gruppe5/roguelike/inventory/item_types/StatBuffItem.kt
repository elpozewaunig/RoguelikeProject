package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff
import com.gruppe5.roguelike.property.Buff
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("statbuff")
class StatBuffItem(
    override val label: String,
    override val imageResId: Int,
    val buff: Buff,
    override val isPermanent: Boolean = false,
) : ItemDefinition() {
    //buff verweist per referenz auf sein item (perma-buff <-> item linkage)
    override fun onUse(instance: ItemInstance, player: Player): List<ActiveBuff> =
        listOf(ActiveBuff(statsMod = buff.statsMod, remainingTurns = buff.duration, sourceItem = instance))
}
