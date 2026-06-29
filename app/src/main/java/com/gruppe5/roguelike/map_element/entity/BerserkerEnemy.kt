package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

/** Greift alles an, auch die eigenen */
class BerserkerEnemy(
    stats: StatModifier,
    position: Position,
) : ChaseEnemy(stats, position, R.drawable.entity_berserker) {
    override val targets: Set<Group> = setOf(Group.PLAYER, Group.ENEMY, Group.NEUTRAL, Group.PLAYERFRIEND)
}
