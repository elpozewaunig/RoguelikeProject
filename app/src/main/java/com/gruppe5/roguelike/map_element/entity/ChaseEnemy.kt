package com.gruppe5.roguelike.map_element.entity

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier
import com.gruppe5.roguelike.turn.Action
import com.gruppe5.roguelike.turn.TurnContext
import com.gruppe5.roguelike.utility.Pathfinding
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Chaser : Enemy() {
    override val resId: Int get() = R.drawable.entity_teto

    protected open val moves: List<Position> get() = Pathfinding.ORTHOGONAL_MOVES
    protected open val heuristic: (Position, Position) -> Double get() = Pathfinding.MANHATTAN
    protected open val ignoreWalls: Boolean get() = false

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
// /\ des muss so schiach sein, siehe enemy \/
@Serializable
@SerialName("chase")
class ChaseEnemy(
    override var stats: StatModifier,
    override var position: Position,
) : Chaser()
