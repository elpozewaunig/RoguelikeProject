package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import kotlin.math.abs

class RangeSightedEnemy(
    stats: StatModifier,
    position: Position,
    val findLineOfSight: Int,
    val loseLineOfSight: Int,
    resId: Int = R.drawable.entity_ranged
) : ChaseEnemy(stats, position, resId) {

    constructor(
        stats: StatModifier,
        position: Position,
        lineOfSight: Int,
        resId: Int = R.drawable.entity_ranged
    ) : this(stats, position, lineOfSight, lineOfSight, resId)

    private var chasing: Boolean = false

    override fun move(map: List<List<MapTile>>, entities: List<Entity>, playerPosition: Position): Position {
        val distance = abs(position.x - playerPosition.x) + abs(position.y - playerPosition.y)
        
        if (chasing) {
            if (distance > loseLineOfSight) {
                chasing = false
            }
        } else {
            if (distance <= findLineOfSight) {
                chasing = true
            }
        }
        
        if (chasing) {
            return super.move(map, entities, playerPosition)
        }
        
        return position
    }
}
