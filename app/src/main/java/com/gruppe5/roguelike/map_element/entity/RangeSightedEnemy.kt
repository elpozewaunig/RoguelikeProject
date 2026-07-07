package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ranged")
class RangeSightedEnemy(
    override var stats: StatModifier,
    override var position: Position,
    val findLineOfSight: Int,
    val loseLineOfSight: Int,
) : Chaser() {
    override val resId: Int get() = R.drawable.entity_ranged
    var chasing: Boolean = false //interner zustand, muss persistiert werden

    constructor(stats: StatModifier, position: Position, lineOfSight: Int)
        : this(stats, position, lineOfSight, lineOfSight)

    override fun act(ctx: TurnContext, times: Int): List<Action> {
        val target = ctx.nearestTo(position, targets, this) ?: return listOf(Action.Wait)
        val distance = position.distanceTo(target.position)

        if (chasing) {
            if (distance > loseLineOfSight) chasing = false
        } else {
            if (distance <= findLineOfSight) chasing = true
        }

        return if (chasing) super.act(ctx, times) else listOf(Action.Wait)
    }
}
