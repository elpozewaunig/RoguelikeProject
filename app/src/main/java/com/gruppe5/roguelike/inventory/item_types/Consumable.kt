package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Effect
import kotlinx.serialization.Serializable

@Serializable
sealed class Consumable : ItemDefinition() {
    abstract fun onUse(player: Player): List<Effect> //returned effects werden von der engine applied
}
