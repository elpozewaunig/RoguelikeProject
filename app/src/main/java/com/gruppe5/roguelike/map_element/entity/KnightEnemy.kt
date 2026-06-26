package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.utility.Pathfinding

/** Der aus Schach */
class KnightEnemy(
    stats: StatModifier,
    position: Position,
) : ChaseEnemy(stats, position, R.drawable.entity_knight) {
    override val moves: List<Position> = Pathfinding.KNIGHT_MOVES
}
