package com.gruppe5.roguelike.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.ActiveBuff

abstract class ItemDefinition(
    open val label: String,
    open val imageResId: Int,
    open val isPermanent: Boolean = false
) {
    abstract fun onUse(instance: ItemInstance, player: Player): List<ActiveBuff>
}

class ItemInstance(
    val definition: ItemDefinition,
    initialUsages: Int
) {
    var usages by mutableStateOf(initialUsages)
    val label: String get() = definition.label
    val imageResId: Int get() = definition.imageResId
    val isPermanent: Boolean get() = definition.isPermanent

    fun use(player: Player): List<ActiveBuff> = definition.onUse(this, player) //gibt Buffs zruck
}

