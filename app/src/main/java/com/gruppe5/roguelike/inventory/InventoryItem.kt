package com.gruppe5.roguelike.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gruppe5.roguelike.map_element.entity.Player

abstract class InventoryItem(
    open val label: String,
    open val imageResId: Int,
    initialUsages: Int
) {
    open var usages by mutableStateOf(initialUsages) //damit sich korrekt in der Hotbar updated
    abstract fun use(player: Player)
}
