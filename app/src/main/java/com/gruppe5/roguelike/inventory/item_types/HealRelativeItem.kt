package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("heal_relative")
class HealRelativeItem(
    override val label: String,
    override val imageResId: Int,
    val healthPercentage: Int,
    override val isPermanent: Boolean = false,
) : ItemDefinition() {
    override fun onUse(instance: ItemInstance, player: Player): List<ActiveBuff> {
        val healAmount = (player.stats.maxHealth * healthPercentage / 100)
        val newHealth = (player.stats.health + healAmount).coerceAtMost(player.stats.maxHealth)
        player.stats.health = newHealth
        return emptyList()
    }
}
