package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** gradle build running (eher slow-walking)*/
@Serializable
@SerialName("elephant")
class ElephantEnemy(
    override var stats: StatModifier,
    override var position: Position,
) : Chaser() {
    override val resId: Int get() = R.drawable.entity_elephant
    override val speed: Float get() = 0.25f
}
