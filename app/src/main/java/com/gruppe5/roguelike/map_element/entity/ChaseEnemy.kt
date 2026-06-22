package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.utility.Pathfinding

class ChaseEnemy(
    override var stats: StatModifier,
    override var position: Position,
    override val resId: Int = R.drawable.entity_teto
): Enemy(stats, position) {
    override fun move(map: List<List<MapTile>>, playerPosition: Position): Position {
        val path = Pathfinding.findPath(map, position, playerPosition)
        return if(path.size > 1) path[1] else position
    }
}
