package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.InventoryItem
import com.gruppe5.roguelike.map_element.entity.Player

open class HealAbsoluteItem(
    label: String,
    imageResId: Int,
    usages: Int,
    var healthAmount: Int
) : InventoryItem(label, imageResId, usages) {

    override fun use(player: Player) {
        val newHealth = (player.stats.health + healthAmount).coerceAtMost(player.stats.maxHealth)
        player.stats.health = newHealth
    }
}
