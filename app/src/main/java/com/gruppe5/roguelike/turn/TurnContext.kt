package com.gruppe5.roguelike.turn

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Enemy
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.property.Position

// overengineered eig aber a contract schadet nie
data class TurnContext(
    val map: List<List<MapTile>>,
    val enemies: List<Enemy>,
    val player: Player,
) {
    fun getEntityAt(pos: Position): Entity? =
        if (player.position == pos) player
        else enemies.firstOrNull { it.position == pos }
}
