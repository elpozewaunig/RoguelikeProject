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

    protected open val ignoreWalls: Boolean = false

    override fun decideAction(ctx: TurnContext): List<Action> {
        if (moves.any { position + it == ctx.player.position }) {
            return listOf(Action.Attack(ctx.player))
        }

        path = Pathfinding.findPath(
            ctx.map,
            ctx.enemies,
            position,
            ctx.player.position,
            moves,
            ignoreWalls
        )
        return if (path.size > 1) listOf(Action.Move(path[1])) else listOf(Action.Wait)
    }
}
