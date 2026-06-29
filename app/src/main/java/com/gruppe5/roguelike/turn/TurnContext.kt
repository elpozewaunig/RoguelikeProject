package com.gruppe5.roguelike.turn

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.property.Position

// overengineered eig aber a contract schadet nie
data class TurnContext(
    val map: List<List<MapTile>>,
    val entities: List<Entity>,
) {
    fun getEntityAt(pos: Position): Entity? = entities.firstOrNull { it.position == pos }

    fun nearestTo(from: Position, groups: Set<Group>, self: Entity): Entity? =
        entities.filter { it !== self && it.groups.any(groups::contains) }
            .minByOrNull { from.distanceTo(it.position) }
}
