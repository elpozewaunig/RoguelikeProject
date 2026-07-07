package com.gruppe5.roguelike.inventory.item_types

import com.gruppe5.roguelike.inventory.EquipSlot
import com.gruppe5.roguelike.inventory.Rarity
import com.gruppe5.roguelike.property.StatModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("equipment")
class EquipmentItem(
    override val label: String,
    override val imageResId: Int,
    override val slot: EquipSlot,
    override val statsMod: StatModifier,
    override val rarity: Rarity = Rarity.COMMON,
) : Equipment()
