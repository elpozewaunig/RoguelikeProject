package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Group
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Greift alles an, auch die eigenen */
@Serializable
@SerialName("berserker")
class BerserkerEnemy(
    override var stats: StatModifier,
    override var position: Position,
) : Chaser() {
    override val resId: Int get() = R.drawable.entity_berserker
    override val targets: Set<Group> get() = setOf(Group.PLAYER, Group.ENEMY, Group.NEUTRAL, Group.PLAYERFRIEND)
}
