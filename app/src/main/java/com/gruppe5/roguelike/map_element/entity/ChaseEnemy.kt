package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.utility.Pathfinding

open class ChaseEnemy(
    override var stats: StatModifier,
    override var position: Position,
    override val resId: Int = R.drawable.entity_teto
) : Enemy(stats, position) {


    protected open val moves: List<Position> = Pathfinding.ORTHOGONAL_MOVES

    protected open val heuristic: (Position, Position) -> Double = Pathfinding.MANHATTAN

    protected open val ignoreWalls: Boolean = false

    override fun act(ctx: TurnContext, times: Int): List<Action> {
        val target = ctx.nearestTo(position, targets, this) ?: return listOf(Action.Wait)

        if (moves.any { position + it == target.position }) {
            return List(times) { Action.Attack(target) }
        }

        path = Pathfinding.findPath(
            ctx.map,
            ctx.entities - target,
            position,
            target.position,
            moves,
            heuristic,
            ignoreWalls
        )
        return path.drop(1).take(times).map { Action.Move(it) }.ifEmpty { listOf(Action.Wait) }
    }
}
