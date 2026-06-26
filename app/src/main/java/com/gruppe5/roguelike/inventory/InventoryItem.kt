package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.map_element.entity.Player

abstract class InventoryItem(
    open val label: String,
    open val imageResId: Int
) {
    abstract fun use(player: Player)
}
