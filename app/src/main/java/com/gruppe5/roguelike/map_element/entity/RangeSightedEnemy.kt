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
//TODO seal-re-audit
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
