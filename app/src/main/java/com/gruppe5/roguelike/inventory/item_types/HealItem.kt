package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.Rarity
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Effect
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("heal")
class HealItem(
    override val label: String,
    override val imageResId: Int,
    val amount: Int,
    val percent: Boolean = false, //true -> amount is % von maxHealth
    override val rarity: Rarity = Rarity.COMMON,
) : Consumable() {
    override fun onUse(player: Player): List<Effect> {
        player.heal(if (percent) player.stats.maxHealth * amount / 100 else amount)
        return emptyList()
    }
}
