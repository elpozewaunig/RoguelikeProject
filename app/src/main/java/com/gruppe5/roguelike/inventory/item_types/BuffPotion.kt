package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.Rarity
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Effect
import com.gruppe5.roguelike.property.StatModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("buffpotion")
class BuffPotion(
    override val label: String,
    override val imageResId: Int,
    val statsMod: StatModifier,
    val duration: Int,
    override val rarity: Rarity = Rarity.COMMON,
) : Consumable() {
    override fun onUse(player: Player): List<Effect> =
        listOf(Effect(label, imageResId, statsMod, duration))
}
