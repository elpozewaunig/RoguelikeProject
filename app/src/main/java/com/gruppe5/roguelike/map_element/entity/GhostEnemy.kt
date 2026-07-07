package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A* chaser mit noclip, dass er in der wand nicht attacked werden kann ist in hindsight eig. ein feature */
@Serializable
@SerialName("ghost")
class GhostEnemy(
    override var stats: StatModifier,
    override var position: Position,
) : Chaser() {
    override val resId: Int get() = R.drawable.entity_ghost
    override val ignoreWalls: Boolean get() = true
    override val speed: Float get() = 2f
}
