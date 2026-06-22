package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.property.Position

interface AIEntity: Entity {
    fun move(map: List<List<MapTile>>, playerPosition: Position): Position
}
