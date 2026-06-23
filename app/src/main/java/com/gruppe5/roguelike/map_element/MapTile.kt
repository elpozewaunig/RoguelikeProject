package com.gruppe5.roguelike.map_element

import com.gruppe5.roguelike.level_generators.TileType
import com.gruppe5.roguelike.property.Position

class MapTile(val type: TileType, override val position: Position) : VisualMapElement {
    override val resId = type.resId
    var discovered: Boolean = false
}
