package com.gruppe5.roguelike.map_element

import androidx.room.Entity
import com.gruppe5.roguelike.level_generators.TileType
import com.gruppe5.roguelike.property.Position

class MapTile(val type: TileType, override val position: Position) : VisualMapElement {
    override val resId = type.resId
    var discovered: Boolean = false

    fun toEntity(): MapTileEntity = MapTileEntity(position.x, position.y, type.name, discovered)
}

@Entity(tableName = "map_tiles", primaryKeys = ["x", "y"]) //x,y ist natural composite key
data class MapTileEntity(
    val x: Int,
    val y: Int,
    val type: String, //TileType.name
    val discovered: Boolean,
) {
    fun toTile(): MapTile = MapTile(TileType.valueOf(type), Position(x, y))
        .also { it.discovered = discovered }
}
