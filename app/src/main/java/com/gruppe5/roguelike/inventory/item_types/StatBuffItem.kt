package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.InventoryItem
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Buff

open class StatBuffItem(
    label: String,
    imageResId: Int,
    usages: Int,
    val buff: Buff //TODO: der läuft zurzeit unendlich, duration weat ignoriert
) : InventoryItem(label, imageResId, usages) {

    override fun use(player: Player) {
        player.stats.attack += buff.statsMod.attack
        player.stats.defense += buff.statsMod.defense
        player.stats.intellect += buff.statsMod.intellect
        player.stats.stealth += buff.statsMod.stealth
        player.stats.speed += buff.statsMod.speed
    }
}
