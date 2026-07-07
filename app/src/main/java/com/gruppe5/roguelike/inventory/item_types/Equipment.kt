package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.EquipSlot
import com.gruppe5.roguelike.property.StatModifier
import kotlinx.serialization.Serializable

@Serializable
sealed class Equipment : ItemDefinition() {
    abstract val slot: EquipSlot
    abstract val statsMod: StatModifier
}
