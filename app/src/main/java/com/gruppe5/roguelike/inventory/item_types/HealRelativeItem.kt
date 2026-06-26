package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.InventoryItem
import com.gruppe5.roguelike.map_element.entity.Player

open class HealRelativeItem(
    label: String,
    imageResId: Int,
    var healthPercentage: Int
) : InventoryItem(label, imageResId) {

    override fun use(player: Player) {
        val healAmount = (player.stats.maxHealth * healthPercentage / 100)
        val newHealth = (player.stats.health + healAmount).coerceAtMost(player.stats.maxHealth)
        player.stats.health = newHealth
    }
}
