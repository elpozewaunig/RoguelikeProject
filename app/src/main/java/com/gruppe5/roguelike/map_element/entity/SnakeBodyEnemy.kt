package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.utility.Pathfinding

/** Schwanzsegment: bleibt liegen, beißt jede Runde, verfällt nach l Runden */
class SnakeBodyEnemy(
    stats: StatModifier,
    position: Position,
    private var lifetime: Int,
) : Enemy(stats, position) {
    override val resId: Int = R.drawable.entity_snakebody

    override fun decideAction(ctx: TurnContext): List<Action> {
        val actions = mutableListOf<Action>()
        val target = ctx.nearestTo(position, targets, this)
        if (target != null && Pathfinding.ORTHOGONAL_MOVES.any { position + it == target.position }) {
            actions += Action.Attack(target)
        }
        if (--lifetime <= 0) actions += Action.Die
        return actions.ifEmpty { listOf(Action.Wait) }
    }
}
