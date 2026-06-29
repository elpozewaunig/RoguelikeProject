package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext

/** Tarnt sich als die Kachel, auf der er spawnt, bis der Spieler nah ist */
class MimicEnemy(
    stats: StatModifier,
    position: Position,
    dormantResId: Int,
) : ChaseEnemy(stats, position) {
    override var resId: Int = dormantResId
    private var awake = false

    override fun decideAction(ctx: TurnContext): List<Action> {
        if (!awake) {
            val target = ctx.nearestTo(position, targets, this) ?: return listOf(Action.Wait)
            if (position.distanceTo(target.position) > 2) return listOf(Action.Wait)
            awake = true
            resId = R.drawable.entity_mimic
        }
        return super.decideAction(ctx)
    }
}
