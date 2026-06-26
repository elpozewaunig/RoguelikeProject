package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

/** A* chaser mit noclip, dass er in der wand nicht attacked werden kann ist in hindsight eig. ein feature */
class GhostEnemy(
    stats: StatModifier,
    position: Position,
) : ChaseEnemy(stats, position, R.drawable.entity_ghost) {
    override val ignoreWalls: Boolean = true
}
