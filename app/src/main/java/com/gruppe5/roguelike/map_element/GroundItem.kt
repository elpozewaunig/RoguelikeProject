package com.gruppe5.roguelike.map_element

import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.property.Position

data class GroundItem(
    override val resId: Int,
    override val position: Position,
    val itemInstance: ItemInstance
): VisualMapElement {
}
