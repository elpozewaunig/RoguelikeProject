package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext

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

    override fun decideAction(ctx: TurnContext): List<Action> {
        val distance = position.distanceTo(ctx.player.position)

        if (chasing) {
            if (distance > loseLineOfSight) chasing = false
        } else {
            if (distance <= findLineOfSight) chasing = true
        }

        return if (chasing) super.decideAction(ctx) else listOf(Action.Wait)
    }
}
