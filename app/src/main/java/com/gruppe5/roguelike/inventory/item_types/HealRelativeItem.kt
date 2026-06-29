package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.ItemDefinition
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff

class HealRelativeItem(
    label: String,
    imageResId: Int,
    val healthPercentage: Int,
    isPermanent: Boolean = false
) : ItemDefinition(label, imageResId, isPermanent) {

    override fun onUse(instance: ItemInstance, player: Player): List<ActiveBuff> {
        val healAmount = (player.stats.maxHealth * healthPercentage / 100)
        val newHealth = (player.stats.health + healAmount).coerceAtMost(player.stats.maxHealth)
        player.stats.health = newHealth
        return emptyList()
    }
}
