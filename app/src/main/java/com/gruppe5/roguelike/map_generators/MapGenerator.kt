package com.gruppe5.roguelike.map_generators

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.property.Position

interface MapGenerator {
    fun getMap(): List<List<MapTile>>
    fun getEnemies(): List<Enemy>
    fun getStartPos(): Position
}
